package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.NoorFlixDatabase
import com.example.data.local.WatchHistoryEntity
import com.example.data.model.*
import com.example.data.repository.NoorFlixRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class HomeCmsConfig(
    val heroDramaId: String = "drama_billionaire_bride",
    val showTrendingRow: Boolean = true,
    val showOriginalsRow: Boolean = true,
    val showBillionaireRow: Boolean = true,
    val showRevengeRow: Boolean = true,
    val showVipRow: Boolean = true
)

data class UiMessage(
    val id: String = UUID.randomUUID().toString(),
    val message: String,
    val isError: Boolean = false
)

class NoorFlixViewModel(application: Application) : AndroidViewModel(application) {
    private val database = NoorFlixDatabase.getDatabase(application)
    val repository = NoorFlixRepository(database, viewModelScope)

    // Current User
    val currentUser: StateFlow<UserProfile> = repository.getUser("usr_001")
        .filterNotNull()
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            UserProfile(id = "usr_001", username = "Amira Al-Mansoor", email = "amira@noorflix.com", coins = 240, isVip = true)
        )

    // Dramas
    val allDramas: StateFlow<List<Drama>> = repository.allDramas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Bookmarks
    val bookmarks: StateFlow<List<String>> = repository.allBookmarks
        .map { list -> list.map { it.dramaId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Watch History
    val watchHistory: StateFlow<List<WatchHistoryEntity>> = repository.watchHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications
    val notifications: StateFlow<List<NotificationItem>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Wallet Transactions
    val transactions: StateFlow<List<CoinTransaction>> = repository.transactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // CMS Config
    private val _cmsConfig = MutableStateFlow(HomeCmsConfig())
    val cmsConfig: StateFlow<HomeCmsConfig> = _cmsConfig.asStateFlow()

    // Active Screen & Navigation State
    private val _selectedDrama = MutableStateFlow<Drama?>(null)
    val selectedDrama: StateFlow<Drama?> = _selectedDrama.asStateFlow()

    private val _selectedEpisode = MutableStateFlow<Episode?>(null)
    val selectedEpisode: StateFlow<Episode?> = _selectedEpisode.asStateFlow()

    // Episodes for current selected drama
    val currentDramaEpisodes: StateFlow<List<Episode>> = _selectedDrama
        .flatMapLatest { drama ->
            if (drama == null) flowOf(emptyList())
            else repository.getEpisodes(drama.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Discover Filters
    val selectedGenreFilter = MutableStateFlow("All")
    val selectedStatusFilter = MutableStateFlow("All") // "All", "Free", "VIP", "Completed"
    val searchQuery = MutableStateFlow("")

    // Player state
    val isPlayerControlsVisible = MutableStateFlow(true)
    val playbackSpeed = MutableStateFlow(1.0f)
    val autoPlayCountdown = MutableStateFlow<Int?>(null)
    private var autoPlayJob: Job? = null

    // UI Feedback Message
    private val _uiMessage = MutableStateFlow<UiMessage?>(null)
    val uiMessage: StateFlow<UiMessage?> = _uiMessage.asStateFlow()

    // Language / RTL Support
    val currentLanguage = MutableStateFlow("English") // "English", "العربية"
    val isArabic: StateFlow<Boolean> = currentLanguage.map { it == "العربية" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun setLanguage(lang: String) {
        currentLanguage.value = lang
        viewModelScope.launch {
            repository.updateUser(currentUser.value.copy(preferredLanguage = lang))
        }
    }

    fun showMessage(msg: String, isError: Boolean = false) {
        _uiMessage.value = UiMessage(message = msg, isError = isError)
    }

    fun clearMessage() {
        _uiMessage.value = null
    }

    fun selectDrama(drama: Drama) {
        _selectedDrama.value = drama
    }

    fun selectEpisode(episode: Episode) {
        _selectedEpisode.value = episode
        autoPlayCountdown.value = null
        autoPlayJob?.cancel()
    }

    fun toggleBookmark(dramaId: String) {
        viewModelScope.launch {
            val isCurrentlyBookmarked = bookmarks.value.contains(dramaId)
            repository.toggleBookmark(dramaId, isCurrentlyBookmarked)
            showMessage(
                if (isCurrentlyBookmarked) "Removed from My List" else "Added to My List ✨"
            )
        }
    }

    fun unlockEpisode(episode: Episode, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val success = repository.unlockEpisode(currentUser.value.id, episode)
            if (success) {
                showMessage("🎉 Episode unlocked successfully!")
                onSuccess()
            } else {
                showMessage("Insufficient coins. Please recharge your wallet.", isError = true)
            }
        }
    }

    fun buyCoins(pkg: CoinPackage) {
        viewModelScope.launch {
            val totalCoins = pkg.coins + pkg.bonusCoins
            repository.addCoins(
                userId = currentUser.value.id,
                coins = totalCoins,
                type = "PURCHASE",
                description = "Purchased ${pkg.coins} coins (+${pkg.bonusCoins} bonus)"
            )
            showMessage("✨ Successfully purchased $totalCoins Coins!")
        }
    }

    fun claimDailyReward() {
        viewModelScope.launch {
            repository.addCoins(
                userId = currentUser.value.id,
                coins = 40,
                type = "DAILY_REWARD",
                description = "Claimed Daily Login Streak (+40 Coins)"
            )
            showMessage("🎁 +40 Coins added to your wallet!")
        }
    }

    fun watchRewardedAd() {
        viewModelScope.launch {
            showMessage("Watching sponsor video reward...")
            delay(1500)
            repository.addCoins(
                userId = currentUser.value.id,
                coins = 20,
                type = "REWARDED_AD",
                description = "Bonus for watching sponsored drama clip (+20 Coins)"
            )
            showMessage("🌟 Reward Claimed! +20 Coins received.")
        }
    }

    fun subscribeVip(plan: VipPlan) {
        viewModelScope.launch {
            repository.activateVip(currentUser.value.id, plan.durationDays)
            showMessage("👑 VIP Membership Activated: ${plan.title}!")
        }
    }

    fun updateWatchProgress(posMs: Long, durationMs: Long) {
        val drama = _selectedDrama.value ?: return
        val ep = _selectedEpisode.value ?: return
        viewModelScope.launch {
            repository.saveWatchProgress(
                dramaId = drama.id,
                episodeId = ep.id,
                episodeNumber = ep.episodeNumber,
                currentPosMs = posMs,
                durationMs = durationMs
            )
        }
    }

    fun playNextEpisode(): Boolean {
        val currentEp = _selectedEpisode.value ?: return false
        val episodes = currentDramaEpisodes.value
        val nextEp = episodes.firstOrNull { it.episodeNumber == currentEp.episodeNumber + 1 }
        return if (nextEp != null) {
            selectEpisode(nextEp)
            true
        } else {
            false
        }
    }

    fun playPreviousEpisode(): Boolean {
        val currentEp = _selectedEpisode.value ?: return false
        val episodes = currentDramaEpisodes.value
        val prevEp = episodes.firstOrNull { it.episodeNumber == currentEp.episodeNumber - 1 }
        return if (prevEp != null) {
            selectEpisode(prevEp)
            true
        } else {
            false
        }
    }

    fun startAutoPlayNextCountdown(onTriggerNext: () -> Unit) {
        autoPlayJob?.cancel()
        autoPlayJob = viewModelScope.launch {
            for (i in 5 downTo 1) {
                autoPlayCountdown.value = i
                delay(1000)
            }
            autoPlayCountdown.value = null
            onTriggerNext()
        }
    }

    fun cancelAutoPlay() {
        autoPlayJob?.cancel()
        autoPlayCountdown.value = null
    }

    // --- Admin Operations ---
    fun adminAddDrama(drama: Drama) {
        viewModelScope.launch {
            repository.saveOrUpdateDrama(drama)
            showMessage("Drama '${drama.title}' published to NoorFlix!")
        }
    }

    fun adminDeleteDrama(dramaId: String) {
        viewModelScope.launch {
            repository.deleteDrama(dramaId)
            showMessage("Drama deleted.")
        }
    }

    fun adminGrantCoins(amount: Int) {
        viewModelScope.launch {
            repository.addCoins(
                userId = currentUser.value.id,
                coins = amount,
                type = "ADMIN_GRANT",
                description = "Admin granted $amount coins"
            )
            showMessage("Admin granted $amount coins to ${currentUser.value.username}!")
        }
    }

    fun adminToggleVip() {
        viewModelScope.launch {
            val user = currentUser.value
            val newVip = !user.isVip
            val newExpiry = if (newVip) System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000) else null
            repository.updateUser(user.copy(isVip = newVip, vipExpiryTimestamp = newExpiry))
            showMessage("VIP status changed to: $newVip")
        }
    }

    fun updateCmsConfig(config: HomeCmsConfig) {
        _cmsConfig.value = config
        showMessage("Home Screen CMS updated!")
    }
}

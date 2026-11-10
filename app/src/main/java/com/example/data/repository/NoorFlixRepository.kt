package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

class NoorFlixRepository(
    private val database: NoorFlixDatabase,
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val dramaDao = database.dramaDao()
    private val episodeDao = database.episodeDao()
    private val watchHistoryDao = database.watchHistoryDao()
    private val bookmarkDao = database.bookmarkDao()
    private val transactionDao = database.coinTransactionDao()
    private val notificationDao = database.notificationDao()
    private val userDao = database.userDao()

    init {
        externalScope.launch {
            seedInitialDataIfEmpty()
        }
    }

    // --- Dramas ---
    val allDramas: Flow<List<Drama>> = dramaDao.getAllDramas().map { list ->
        list.map { it.toDomain() }
    }

    suspend fun getDrama(id: String): Drama? {
        return dramaDao.getDramaById(id)?.toDomain()
    }

    // --- Episodes ---
    fun getEpisodes(dramaId: String): Flow<List<Episode>> {
        return episodeDao.getEpisodesForDrama(dramaId).map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun unlockEpisode(userId: String, episode: Episode): Boolean {
        val user = userDao.getUserOnce(userId) ?: return false
        if (user.coins < episode.coinPrice) return false

        val newBalance = user.coins - episode.coinPrice
        userDao.updateCoins(userId, newBalance)
        episodeDao.markEpisodeUnlocked(episode.id)

        transactionDao.insertTransaction(
            CoinTransactionEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                amount = -episode.coinPrice,
                type = "EPISODE_UNLOCK",
                description = "Unlocked Episode ${episode.episodeNumber}: ${episode.title}",
                timestamp = System.currentTimeMillis()
            )
        )
        return true
    }

    // --- Watch History ---
    val watchHistory: Flow<List<WatchHistoryEntity>> = watchHistoryDao.getAllHistory()

    suspend fun saveWatchProgress(
        dramaId: String,
        episodeId: String,
        episodeNumber: Int,
        currentPosMs: Long,
        durationMs: Long
    ) {
        watchHistoryDao.saveHistory(
            WatchHistoryEntity(
                dramaId = dramaId,
                episodeId = episodeId,
                episodeNumber = episodeNumber,
                currentPositionMs = currentPosMs,
                durationMs = durationMs,
                lastWatchedTimestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun getWatchProgress(dramaId: String): WatchHistoryEntity? {
        return watchHistoryDao.getHistoryForDrama(dramaId)
    }

    // --- Bookmarks (My List) ---
    val allBookmarks: Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()

    fun isBookmarked(dramaId: String): Flow<Boolean> = bookmarkDao.isBookmarked(dramaId)

    suspend fun toggleBookmark(dramaId: String, bookmarked: Boolean) {
        if (bookmarked) {
            bookmarkDao.removeBookmark(dramaId)
        } else {
            bookmarkDao.addBookmark(BookmarkEntity(dramaId = dramaId))
        }
    }

    // --- Wallet & Transactions ---
    val transactions: Flow<List<CoinTransaction>> = transactionDao.getAllTransactions().map { list ->
        list.map {
            CoinTransaction(
                id = it.id,
                userId = it.userId,
                amount = it.amount,
                type = it.type,
                description = it.description,
                timestamp = it.timestamp
            )
        }
    }

    suspend fun addCoins(userId: String, coins: Int, type: String, description: String) {
        val user = userDao.getUserOnce(userId) ?: return
        val newBalance = user.coins + coins
        userDao.updateCoins(userId, newBalance)

        transactionDao.insertTransaction(
            CoinTransactionEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                amount = coins,
                type = type,
                description = description,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    // --- VIP Membership ---
    suspend fun activateVip(userId: String, days: Int) {
        val expiry = System.currentTimeMillis() + (days.toLong() * 24L * 60L * 60L * 1000L)
        userDao.updateVipStatus(userId, isVip = true, expiry = expiry)

        notificationDao.insertNotifications(
            listOf(
                NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    title = "VIP Membership Activated!",
                    message = "Welcome to NoorFlix VIP! Enjoy unlimited VIP episodes and ad-free experience.",
                    timestamp = System.currentTimeMillis(),
                    type = "VIP_PERK",
                    isRead = false
                )
            )
        )
    }

    // --- User Profile ---
    fun getUser(id: String): Flow<UserProfile?> = userDao.getUser(id).map { it?.toDomain() }

    suspend fun updateUser(user: UserProfile) {
        userDao.insertOrUpdateUser(user.toEntity())
    }

    // --- Notifications ---
    val notifications: Flow<List<NotificationItem>> = notificationDao.getAllNotifications().map { list ->
        list.map {
            NotificationItem(
                id = it.id,
                title = it.title,
                message = it.message,
                timestamp = it.timestamp,
                type = it.type,
                isRead = it.isRead
            )
        }
    }

    suspend fun markNotificationAsRead(id: String) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() {
        notificationDao.markAllAsRead()
    }

    // --- Admin CMS Operations ---
    suspend fun saveOrUpdateDrama(drama: Drama) {
        dramaDao.insertDrama(drama.toEntity())
    }

    suspend fun deleteDrama(dramaId: String) {
        dramaDao.deleteDramaById(dramaId)
    }

    suspend fun saveEpisode(episode: Episode) {
        episodeDao.insertEpisode(episode.toEntity())
    }

    suspend fun deleteEpisode(episodeId: String) {
        episodeDao.deleteEpisodeById(episodeId)
    }

    // --- Seeding Data ---
    private suspend fun seedInitialDataIfEmpty() {
        val existingUser = userDao.getUserOnce("usr_001")
        if (existingUser == null) {
            userDao.insertOrUpdateUser(
                UserEntity(
                    id = "usr_001",
                    username = "Amira Al-Mansoor",
                    email = "amira@noorflix.com",
                    avatarUrl = "",
                    coins = 240,
                    isVip = true,
                    vipExpiryTimestamp = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
                    role = "SUPER_ADMIN",
                    preferredLanguage = "English",
                    notificationsEnabled = true
                )
            )

            // Initial transactions
            transactionDao.insertTransaction(
                CoinTransactionEntity(
                    id = "tx_welcome",
                    userId = "usr_001",
                    amount = 200,
                    type = "WELCOME_BONUS",
                    description = "NoorFlix Sign-up Welcome Gift",
                    timestamp = System.currentTimeMillis() - 86400000
                )
            )
            transactionDao.insertTransaction(
                CoinTransactionEntity(
                    id = "tx_daily",
                    userId = "usr_001",
                    amount = 40,
                    type = "DAILY_REWARD",
                    description = "Day 1 Daily Check-in Streak",
                    timestamp = System.currentTimeMillis() - 3600000
                )
            )

            // Notifications
            notificationDao.insertNotifications(
                listOf(
                    NotificationEntity(
                        id = "notif_1",
                        title = "✨ Welcome to NoorFlix!",
                        message = "Your account is loaded with 200 free coins. Start watching today's top dramas!",
                        timestamp = System.currentTimeMillis() - 86400000,
                        type = "COIN_REWARD",
                        isRead = false
                    ),
                    NotificationEntity(
                        id = "notif_2",
                        title = "🔥 New Release: The Billionaire's Secret Bride",
                        message = "All 15 episodes are now streaming in ultra HD. Watch now!",
                        timestamp = System.currentTimeMillis() - 43200000,
                        type = "NEW_EPISODE",
                        isRead = false
                    ),
                    NotificationEntity(
                        id = "notif_3",
                        title = "👑 VIP Status Active",
                        message = "Unlimited premium access granted for 30 days.",
                        timestamp = System.currentTimeMillis() - 10800000,
                        type = "VIP_PERK",
                        isRead = true
                    )
                )
            )

            // Seed Dramas
            val dramas = listOf(
                DramaEntity(
                    id = "drama_billionaire_bride",
                    title = "The Billionaire's Secret Bride",
                    originalTitle = "عروس الملياردير السرية",
                    synopsis = "Forced into a contract marriage with the reclusive billionaire Ethan Vance, Sarah expected a cold business deal. But beneath his ruthless exterior lies an obsessive love and a secret that threatens their entire empire.",
                    posterUrl = "",
                    posterDrawableName = "poster_billionaire_bride",
                    backdropUrl = "",
                    genre = "Billionaire Romance",
                    tagsCsv = "Billionaire,Contract Marriage,CEO,Drama,Secret Identity",
                    totalEpisodes = 15,
                    rating = 4.9,
                    viewsCount = 384000,
                    releaseYear = 2024,
                    status = "COMPLETED",
                    isFeatured = true,
                    isTrending = true,
                    isExclusive = true,
                    isVipOnly = false,
                    language = "English & Arabic Subs",
                    castCsv = "Liam Hemsworth, Lily Collins, Ryan Vance",
                    director = "Omar Al-Khatib"
                ),
                DramaEntity(
                    id = "drama_revenge_ex_wife",
                    title = "Revenge of the Ex-Wife",
                    originalTitle = "انتقام الزوجة السابقة",
                    synopsis = "Betrayed, framed, and left with nothing by her arrogant in-laws, Eliza returns three years later disguised as the ruthless venture capitalist buying out their family dynasty. No mercy will be granted.",
                    posterUrl = "",
                    posterDrawableName = "poster_revenge_ex_wife",
                    backdropUrl = "",
                    genre = "Revenge & Drama",
                    tagsCsv = "Revenge,Strong Female Lead,Betrayal,High Society,Corporate",
                    totalEpisodes = 12,
                    rating = 4.8,
                    viewsCount = 295000,
                    releaseYear = 2024,
                    status = "COMPLETED",
                    isFeatured = false,
                    isTrending = true,
                    isExclusive = true,
                    isVipOnly = false,
                    language = "English & Arabic Subs",
                    castCsv = "Eliza Clarke, Marcus Thorne, Victoria Sterling",
                    director = "Sophia Loren"
                ),
                DramaEntity(
                    id = "drama_hidden_heiress",
                    title = "The Hidden Heiress",
                    originalTitle = "الوريثة الخفية",
                    synopsis = "Growing up as an unassuming intern, Clara Chen suddenly learns she is the sole heiress to Aurora Holdings, the country's wealthiest conglomerate. She decides to keep her identity secret to expose the boardroom conspirators.",
                    posterUrl = "",
                    posterDrawableName = "poster_hidden_heiress",
                    backdropUrl = "",
                    genre = "CEO Stories",
                    tagsCsv = "CEO,Hidden Identity,Wealth,Boardroom,Power Struggle",
                    totalEpisodes = 10,
                    rating = 4.7,
                    viewsCount = 210000,
                    releaseYear = 2024,
                    status = "COMPLETED",
                    isFeatured = false,
                    isTrending = true,
                    isExclusive = false,
                    isVipOnly = true,
                    language = "English & Arabic Subs",
                    castCsv = "Clara Chen, David Reid, Michael Foster",
                    director = "Tariq Mansoor"
                ),
                DramaEntity(
                    id = "drama_dangerous_vows",
                    title = "Dangerous Vows: The Mafia Heir",
                    originalTitle = "عهود خطيرة: وريث المافيا",
                    synopsis = "When innocent violinist Maya witnesses a high-stakes exchange, the formidable underworld heir Lucas Costa makes her an offer: marry him for protection, or face his enemies alone.",
                    posterUrl = "",
                    posterDrawableName = "poster_billionaire_bride",
                    backdropUrl = "",
                    genre = "Mystery & Suspense",
                    tagsCsv = "Enemies to Lovers,Mafia,Protection,Dark Romance",
                    totalEpisodes = 14,
                    rating = 4.9,
                    viewsCount = 420000,
                    releaseYear = 2024,
                    status = "COMPLETED",
                    isFeatured = false,
                    isTrending = false,
                    isExclusive = true,
                    isVipOnly = false,
                    language = "English & Arabic Subs",
                    castCsv = "Julian Rossi, Maya Vance",
                    director = "Christian Bale"
                ),
                DramaEntity(
                    id = "drama_desert_love",
                    title = "Desert Moon: Sheikh's Destiny",
                    originalTitle = "قمر الصحراء: قدر الشيخ",
                    synopsis = "In the breathtaking dunes of Dubai and the luxury skyscrapers of Abu Dhabi, an archaeological researcher uncovers a golden ancient artifact that binds her fate to the Crown Prince.",
                    posterUrl = "",
                    posterDrawableName = "poster_hidden_heiress",
                    backdropUrl = "",
                    genre = "Romance",
                    tagsCsv = "Royal Romance,Desert,Destiny,Arabic,Luxury",
                    totalEpisodes = 16,
                    rating = 4.8,
                    viewsCount = 312000,
                    releaseYear = 2024,
                    status = "ONGOING",
                    isFeatured = false,
                    isTrending = true,
                    isExclusive = true,
                    isVipOnly = false,
                    language = "Arabic with English Subs",
                    castCsv = "Fares Al-Nour, Layla Qasim",
                    director = "Ziad Doueiri"
                ),
                DramaEntity(
                    id = "drama_double_life",
                    title = "Double Life of CEO Martin",
                    originalTitle = "الحياة المزدوجة للمدير",
                    synopsis = "By day, Martin works as a humble coffee barista to see who genuinely cares for him. By night, he commands the multi-billion dollar Apex Global empire.",
                    posterUrl = "",
                    posterDrawableName = "poster_revenge_ex_wife",
                    backdropUrl = "",
                    genre = "Comedy & Romance",
                    tagsCsv = "Undercover,Comedy,Sweet Romance,Billionaire",
                    totalEpisodes = 10,
                    rating = 4.6,
                    viewsCount = 188000,
                    releaseYear = 2024,
                    status = "COMPLETED",
                    isFeatured = false,
                    isTrending = false,
                    isExclusive = false,
                    isVipOnly = false,
                    language = "English",
                    castCsv = "Martin Shaw, Chloe Bennett",
                    director = "James Wan"
                )
            )
            dramaDao.insertDramas(dramas)

            // Seed Episodes with high-quality real streaming sample videos
            val videoUrls = listOf(
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
            )

            val episodes = mutableListOf<EpisodeEntity>()
            dramas.forEach { drama ->
                for (epNum in 1..drama.totalEpisodes) {
                    val accessType = when {
                        epNum <= 3 -> "FREE"
                        epNum <= 8 -> "COIN_UNLOCK"
                        else -> "VIP_ONLY"
                    }
                    val isUnlocked = (epNum <= 3) // First 3 episodes free for all
                    val video = videoUrls[(epNum - 1) % videoUrls.size]
                    episodes.add(
                        EpisodeEntity(
                            id = "${drama.id}_ep_$epNum",
                            dramaId = drama.id,
                            episodeNumber = epNum,
                            title = "Episode $epNum: " + when (epNum) {
                                1 -> "The Unforseen Encounter"
                                2 -> "A Mask of Cold Steel"
                                3 -> "Midnight Revelation"
                                4 -> "Caught in the Crossfire"
                                5 -> "The Contract Exposed"
                                6 -> "Whispers of Betrayal"
                                7 -> "A Dangerous Dance"
                                8 -> "Point of No Return"
                                9 -> "Shattered Alliances"
                                10 -> "The True Mastermind"
                                11 -> "Rising from the Ashes"
                                12 -> "Grand Boardroom Duel"
                                13 -> "Confession at Twilight"
                                14 -> "The Last Gamble"
                                else -> "Eternal Vows"
                            },
                            synopsis = "Key turning points unfold in Episode $epNum as secrets from the past resurface, testing loyalties and sparking unexpected romance.",
                            videoUrl = video,
                            thumbnailUrl = "",
                            thumbnailDrawableName = drama.posterDrawableName,
                            durationSeconds = 120 + (epNum * 15 % 80),
                            accessType = accessType,
                            coinPrice = if (accessType == "COIN_UNLOCK") 20 else 0,
                            isUnlocked = isUnlocked
                        )
                    )
                }
            }
            episodeDao.insertEpisodes(episodes)

            // Seed initial watch history
            watchHistoryDao.saveHistory(
                WatchHistoryEntity(
                    dramaId = "drama_billionaire_bride",
                    episodeId = "drama_billionaire_bride_ep_1",
                    episodeNumber = 1,
                    currentPositionMs = 45000L,
                    durationMs = 135000L,
                    lastWatchedTimestamp = System.currentTimeMillis() - 3600000
                )
            )

            // Seed bookmark
            bookmarkDao.addBookmark(BookmarkEntity(dramaId = "drama_billionaire_bride"))
            bookmarkDao.addBookmark(BookmarkEntity(dramaId = "drama_revenge_ex_wife"))
        }
    }

    // --- Entity Domain Mappers ---
    private fun DramaEntity.toDomain(): Drama = Drama(
        id = id,
        title = title,
        originalTitle = originalTitle,
        synopsis = synopsis,
        posterUrl = posterUrl,
        posterResId = null,
        backdropUrl = backdropUrl,
        backdropResId = null,
        genre = genre,
        tags = if (tagsCsv.isBlank()) emptyList() else tagsCsv.split(","),
        totalEpisodes = totalEpisodes,
        rating = rating,
        viewsCount = viewsCount,
        releaseYear = releaseYear,
        status = if (status == "ONGOING") DramaStatus.ONGOING else DramaStatus.COMPLETED,
        isFeatured = isFeatured,
        isTrending = isTrending,
        isExclusive = isExclusive,
        isVipOnly = isVipOnly,
        language = language,
        cast = if (castCsv.isBlank()) emptyList() else castCsv.split(","),
        director = director
    )

    private fun Drama.toEntity(): DramaEntity = DramaEntity(
        id = id,
        title = title,
        originalTitle = originalTitle,
        synopsis = synopsis,
        posterUrl = posterUrl,
        posterDrawableName = when {
            id.contains("billionaire") -> "poster_billionaire_bride"
            id.contains("revenge") -> "poster_revenge_ex_wife"
            id.contains("heiress") -> "poster_hidden_heiress"
            else -> "poster_billionaire_bride"
        },
        backdropUrl = backdropUrl,
        genre = genre,
        tagsCsv = tags.joinToString(","),
        totalEpisodes = totalEpisodes,
        rating = rating,
        viewsCount = viewsCount,
        releaseYear = releaseYear,
        status = status.name,
        isFeatured = isFeatured,
        isTrending = isTrending,
        isExclusive = isExclusive,
        isVipOnly = isVipOnly,
        language = language,
        castCsv = cast.joinToString(","),
        director = director
    )

    private fun EpisodeEntity.toDomain(): Episode = Episode(
        id = id,
        dramaId = dramaId,
        episodeNumber = episodeNumber,
        title = title,
        synopsis = synopsis,
        videoUrl = videoUrl,
        thumbnailUrl = thumbnailUrl,
        thumbnailResId = null,
        durationSeconds = durationSeconds,
        accessType = when (accessType) {
            "VIP_ONLY" -> AccessType.VIP_ONLY
            "COIN_UNLOCK" -> AccessType.COIN_UNLOCK
            else -> AccessType.FREE
        },
        coinPrice = coinPrice,
        isUnlocked = isUnlocked
    )

    private fun Episode.toEntity(): EpisodeEntity = EpisodeEntity(
        id = id,
        dramaId = dramaId,
        episodeNumber = episodeNumber,
        title = title,
        synopsis = synopsis,
        videoUrl = videoUrl,
        thumbnailUrl = thumbnailUrl,
        thumbnailDrawableName = "",
        durationSeconds = durationSeconds,
        accessType = accessType.name,
        coinPrice = coinPrice,
        isUnlocked = isUnlocked
    )

    private fun UserEntity.toDomain(): UserProfile = UserProfile(
        id = id,
        username = username,
        email = email,
        avatarUrl = avatarUrl,
        coins = coins,
        isVip = isVip,
        vipExpiryTimestamp = vipExpiryTimestamp,
        role = when (role) {
            "SUPER_ADMIN" -> UserRole.SUPER_ADMIN
            "CONTENT_MANAGER" -> UserRole.CONTENT_MANAGER
            "VIP" -> UserRole.VIP
            else -> UserRole.USER
        },
        preferredLanguage = preferredLanguage,
        notificationsEnabled = notificationsEnabled
    )

    private fun UserProfile.toEntity(): UserEntity = UserEntity(
        id = id,
        username = username,
        email = email,
        avatarUrl = avatarUrl,
        coins = coins,
        isVip = isVip,
        vipExpiryTimestamp = vipExpiryTimestamp,
        role = role.name,
        preferredLanguage = preferredLanguage,
        notificationsEnabled = notificationsEnabled
    )
}

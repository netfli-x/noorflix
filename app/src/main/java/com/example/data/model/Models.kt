package com.example.data.model

enum class AccessType {
    FREE,
    COIN_UNLOCK,
    VIP_ONLY
}

enum class DramaStatus {
    ONGOING,
    COMPLETED
}

enum class UserRole {
    USER,
    VIP,
    SUPER_ADMIN,
    CONTENT_MANAGER
}

data class Drama(
    val id: String,
    val title: String,
    val originalTitle: String = "",
    val synopsis: String,
    val posterUrl: String,
    val posterResId: Int? = null,
    val backdropUrl: String = "",
    val backdropResId: Int? = null,
    val genre: String,
    val tags: List<String> = emptyList(),
    val totalEpisodes: Int,
    val rating: Double = 4.8,
    val viewsCount: Long = 124000,
    val releaseYear: Int = 2024,
    val status: DramaStatus = DramaStatus.COMPLETED,
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val isExclusive: Boolean = false,
    val isVipOnly: Boolean = false,
    val language: String = "English",
    val cast: List<String> = emptyList(),
    val director: String = "Noor Studios"
)

data class Episode(
    val id: String,
    val dramaId: String,
    val episodeNumber: Int,
    val title: String,
    val synopsis: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val thumbnailResId: Int? = null,
    val durationSeconds: Int = 120, // Typical short drama episode ~1-3 mins
    val accessType: AccessType = AccessType.FREE,
    val coinPrice: Int = 20,
    val isUnlocked: Boolean = false
)

data class WatchProgress(
    val dramaId: String,
    val episodeId: String,
    val episodeNumber: Int,
    val currentPositionMs: Long,
    val durationMs: Long,
    val lastWatchedTimestamp: Long = System.currentTimeMillis()
)

data class CoinPackage(
    val id: String,
    val coins: Int,
    val bonusCoins: Int = 0,
    val priceUsd: Double,
    val tag: String? = null
)

data class VipPlan(
    val id: String,
    val title: String,
    val durationDays: Int,
    val priceUsd: Double,
    val savingsText: String? = null,
    val features: List<String>
)

data class CoinTransaction(
    val id: String,
    val userId: String,
    val amount: Int,
    val type: String, // "PURCHASE", "EPISODE_UNLOCK", "DAILY_REWARD", "ADMIN_GRANT"
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class UserProfile(
    val id: String = "usr_001",
    val username: String = "NoorWatcher",
    val email: String = "viewer@noorflix.com",
    val avatarUrl: String = "",
    val coins: Int = 150,
    val isVip: Boolean = false,
    val vipExpiryTimestamp: Long? = null,
    val role: UserRole = UserRole.SUPER_ADMIN, // Defaulting to Admin access capable for full functionality
    val preferredLanguage: String = "English",
    val notificationsEnabled: Boolean = true
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "NEW_EPISODE", // "NEW_EPISODE", "COIN_REWARD", "VIP_PERK"
    val isRead: Boolean = false
)

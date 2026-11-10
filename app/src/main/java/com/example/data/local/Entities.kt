package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dramas")
data class DramaEntity(
    @PrimaryKey val id: String,
    val title: String,
    val originalTitle: String,
    val synopsis: String,
    val posterUrl: String,
    val posterDrawableName: String = "",
    val backdropUrl: String = "",
    val genre: String,
    val tagsCsv: String,
    val totalEpisodes: Int,
    val rating: Double,
    val viewsCount: Long,
    val releaseYear: Int,
    val status: String,
    val isFeatured: Boolean,
    val isTrending: Boolean,
    val isExclusive: Boolean,
    val isVipOnly: Boolean,
    val language: String,
    val castCsv: String,
    val director: String
)

@Entity(tableName = "episodes")
data class EpisodeEntity(
    @PrimaryKey val id: String,
    val dramaId: String,
    val episodeNumber: Int,
    val title: String,
    val synopsis: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val thumbnailDrawableName: String = "",
    val durationSeconds: Int,
    val accessType: String,
    val coinPrice: Int,
    val isUnlocked: Boolean
)

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey val dramaId: String,
    val episodeId: String,
    val episodeNumber: Int,
    val currentPositionMs: Long,
    val durationMs: Long,
    val lastWatchedTimestamp: Long
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val dramaId: String,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "coin_transactions")
data class CoinTransactionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val amount: Int,
    val type: String,
    val description: String,
    val timestamp: Long
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val type: String,
    val isRead: Boolean
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val email: String,
    val avatarUrl: String,
    val coins: Int,
    val isVip: Boolean,
    val vipExpiryTimestamp: Long?,
    val role: String,
    val preferredLanguage: String,
    val notificationsEnabled: Boolean
)

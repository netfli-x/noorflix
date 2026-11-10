package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DramaDao {
    @Query("SELECT * FROM dramas ORDER BY rating DESC")
    fun getAllDramas(): Flow<List<DramaEntity>>

    @Query("SELECT * FROM dramas WHERE id = :id")
    suspend fun getDramaById(id: String): DramaEntity?

    @Query("SELECT * FROM dramas WHERE isFeatured = 1 LIMIT 1")
    fun getFeaturedDrama(): Flow<DramaEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDramas(dramas: List<DramaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrama(drama: DramaEntity)

    @Update
    suspend fun updateDrama(drama: DramaEntity)

    @Delete
    suspend fun deleteDrama(drama: DramaEntity)

    @Query("DELETE FROM dramas WHERE id = :id")
    suspend fun deleteDramaById(id: String)
}

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episodes WHERE dramaId = :dramaId ORDER BY episodeNumber ASC")
    fun getEpisodesForDrama(dramaId: String): Flow<List<EpisodeEntity>>

    @Query("SELECT * FROM episodes WHERE id = :id")
    suspend fun getEpisodeById(id: String): EpisodeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisodes(episodes: List<EpisodeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisode(episode: EpisodeEntity)

    @Query("UPDATE episodes SET isUnlocked = 1 WHERE id = :id")
    suspend fun markEpisodeUnlocked(id: String)

    @Update
    suspend fun updateEpisode(episode: EpisodeEntity)

    @Query("DELETE FROM episodes WHERE id = :id")
    suspend fun deleteEpisodeById(id: String)
}

@Dao
interface WatchHistoryDao {
    @Query("SELECT * FROM watch_history ORDER BY lastWatchedTimestamp DESC")
    fun getAllHistory(): Flow<List<WatchHistoryEntity>>

    @Query("SELECT * FROM watch_history WHERE dramaId = :dramaId")
    suspend fun getHistoryForDrama(dramaId: String): WatchHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveHistory(history: WatchHistoryEntity)

    @Query("DELETE FROM watch_history WHERE dramaId = :dramaId")
    suspend fun deleteHistory(dramaId: String)
}

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY addedTimestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE dramaId = :dramaId)")
    fun isBookmarked(dramaId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE dramaId = :dramaId")
    suspend fun removeBookmark(dramaId: String)
}

@Dao
interface CoinTransactionDao {
    @Query("SELECT * FROM coin_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<CoinTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: CoinTransactionEntity)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    fun getUser(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserOnce(id: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserEntity)

    @Query("UPDATE users SET coins = :coins WHERE id = :id")
    suspend fun updateCoins(id: String, coins: Int)

    @Query("UPDATE users SET isVip = :isVip, vipExpiryTimestamp = :expiry WHERE id = :id")
    suspend fun updateVipStatus(id: String, isVip: Boolean, expiry: Long?)
}

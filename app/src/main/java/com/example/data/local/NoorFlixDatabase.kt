package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        DramaEntity::class,
        EpisodeEntity::class,
        WatchHistoryEntity::class,
        BookmarkEntity::class,
        CoinTransactionEntity::class,
        NotificationEntity::class,
        UserEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NoorFlixDatabase : RoomDatabase() {
    abstract fun dramaDao(): DramaDao
    abstract fun episodeDao(): EpisodeDao
    abstract fun watchHistoryDao(): WatchHistoryDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun coinTransactionDao(): CoinTransactionDao
    abstract fun notificationDao(): NotificationDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: NoorFlixDatabase? = null

        fun getDatabase(context: Context): NoorFlixDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoorFlixDatabase::class.java,
                    "noorflix_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

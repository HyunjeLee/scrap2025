package com.scrap2025.scrap2025.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.scrap2025.scrap2025.data.local.dao.CategoryDao
import com.scrap2025.scrap2025.data.local.dao.MyPageDao
import com.scrap2025.scrap2025.data.local.dao.ScrapDao
import com.scrap2025.scrap2025.data.local.entity.CategoryEntity
import com.scrap2025.scrap2025.data.local.entity.MyPageEntity
import com.scrap2025.scrap2025.data.local.entity.ScrapEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Database(
        entities = [CategoryEntity::class, ScrapEntity::class, MyPageEntity::class],
        version = 9,
        exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun scrapDao(): ScrapDao
    abstract fun myPageDao(): MyPageDao

    suspend fun clearAllData() {
        withContext(Dispatchers.IO) { clearAllTables() }
    }

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE
                    ?: synchronized(this) {
                        val instance =
                                Room.databaseBuilder(
                                                context.applicationContext,
                                                AppDatabase::class.java,
                                                "scrap_database"
                                        )
                                        .fallbackToDestructiveMigration()
                                        .build()
                        INSTANCE = instance
                        instance
                    }
        }
    }
}

package com.scrap2025.scrap2025.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.scrap2025.scrap2025.data.local.dao.CategoryDao
import com.scrap2025.scrap2025.data.local.dao.ScrapDao
import com.scrap2025.scrap2025.data.local.entity.CategoryEntity
import com.scrap2025.scrap2025.data.local.entity.ScrapEntity

@Database(entities = [CategoryEntity::class, ScrapEntity::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun scrapDao(): ScrapDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

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

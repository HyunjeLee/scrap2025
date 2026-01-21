package com.scrap2025.scrap2025.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.withTransaction
import com.scrap2025.scrap2025.data.local.dao.CategoryDao
import com.scrap2025.scrap2025.data.local.dao.MyPageDao
import com.scrap2025.scrap2025.data.local.dao.ScrapDao
import com.scrap2025.scrap2025.data.local.entity.CategoryEntity
import com.scrap2025.scrap2025.data.local.entity.MyPageEntity
import com.scrap2025.scrap2025.data.local.entity.ScrapEntity

@Database(
    entities = [CategoryEntity::class, ScrapEntity::class, MyPageEntity::class],
    version = 15,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao

    abstract fun scrapDao(): ScrapDao

    abstract fun myPageDao(): MyPageDao

    suspend fun clearAllData() {
//        withContext(Dispatchers.IO) { clearAllTables() }  // 외래 키 참조로 인해 에러 발생  // 아래 로직으로 수정
        withTransaction {
            // 데이터 삭제 순서를 외래 키 참조 관계의 역순으로 배치
            scrapDao().deleteAll()
            categoryDao().deleteAll()
            myPageDao().clearMyPage()
        }
    }

    companion object {
        @Volatile private var dbInstance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase = dbInstance
            ?: synchronized(this) {
                val instance =
                    Room
                        .databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "scrap_database"
                        ).fallbackToDestructiveMigration()
                        .build()
                dbInstance = instance
                instance
            }
    }
}

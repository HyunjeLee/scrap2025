package com.scrap2025.scrap2025.di

import android.content.Context
import com.scrap2025.scrap2025.data.local.AppDatabase
import com.scrap2025.scrap2025.data.local.dao.CategoryDao
import com.scrap2025.scrap2025.data.local.dao.MyPageDao
import com.scrap2025.scrap2025.data.local.dao.ScrapDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    @Singleton
    fun provideScrapDao(database: AppDatabase): ScrapDao {
        return database.scrapDao()
    }

    @Provides
    @Singleton
    fun provideMyPageDao(database: AppDatabase): MyPageDao {
        return database.myPageDao()
    }
}

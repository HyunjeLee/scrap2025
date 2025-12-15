package com.scrap2025.scrap2025.di

import android.content.Context
import com.scrap2025.scrap2025.data.local.PreferencesManager
import com.scrap2025.scrap2025.repository.CategoryRepository
import com.scrap2025.scrap2025.repository.CategoryRepositoryImpl
import com.scrap2025.scrap2025.repository.LinkPreviewRepository
import com.scrap2025.scrap2025.repository.LinkPreviewRepositoryImpl
import com.scrap2025.scrap2025.repository.ScrapRepository
import com.scrap2025.scrap2025.repository.ScrapRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindScrapRepository(
        scrapRepositoryImpl: ScrapRepositoryImpl
    ): ScrapRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindLinkPreviewRepository(
        linkPreviewRepositoryImpl: LinkPreviewRepositoryImpl
    ): LinkPreviewRepository

    companion object {
        @Provides
        @Singleton
        fun providePreferencesManager(
            @ApplicationContext context: Context
        ): PreferencesManager {
            return PreferencesManager(context)
        }
    }
}

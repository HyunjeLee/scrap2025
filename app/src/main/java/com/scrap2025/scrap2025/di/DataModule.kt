package com.scrap2025.scrap2025.di

import android.content.Context
import com.scrap2025.scrap2025.data.local.PreferencesManager
import com.scrap2025.scrap2025.data.remote.auth.GoogleLoginProvider
import com.scrap2025.scrap2025.data.remote.auth.KakaoLoginProvider
import com.scrap2025.scrap2025.data.remote.auth.NaverLoginProvider
import com.scrap2025.scrap2025.data.remote.auth.SocialLoginProvider
import com.scrap2025.scrap2025.model.SnsType
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
import dagger.multibindings.IntoMap
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

    @Binds
    @IntoMap
    @SnsTypeKey(SnsType.NAVER)
    abstract fun bindNaverLoginProvider(naverLoginProvider: NaverLoginProvider): SocialLoginProvider

    @Binds
    @IntoMap
    @SnsTypeKey(SnsType.KAKAO)
    abstract fun bindKakaoLoginProvider(kakaoLoginProvider: KakaoLoginProvider): SocialLoginProvider

    @Binds
    @IntoMap
    @SnsTypeKey(SnsType.GOOGLE)
    abstract fun bindGoogleLoginProvider(googleLoginProvider: GoogleLoginProvider): SocialLoginProvider

    companion object {
        @Provides
        @Singleton
        fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
            return PreferencesManager(context)
        }
    }
}

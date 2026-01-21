package com.scrap2025.scrap2025.di

import android.content.Context
import com.scrap2025.scrap2025.data.local.PreferencesManager
import com.scrap2025.scrap2025.data.remote.auth.social.GoogleLoginProvider
import com.scrap2025.scrap2025.data.remote.auth.social.KakaoLoginProvider
import com.scrap2025.scrap2025.data.remote.auth.social.NaverLoginProvider
import com.scrap2025.scrap2025.data.remote.auth.social.SocialLoginProvider
import com.scrap2025.scrap2025.data.remote.datasource.AuthRemoteDataSource
import com.scrap2025.scrap2025.data.remote.datasource.AuthRemoteDataSourceImpl
import com.scrap2025.scrap2025.data.remote.datasource.CategoryRemoteDataSource
import com.scrap2025.scrap2025.data.remote.datasource.CategoryRemoteDataSourceImpl
import com.scrap2025.scrap2025.data.remote.datasource.ScrapRemoteDataSource
import com.scrap2025.scrap2025.data.remote.datasource.ScrapRemoteDataSourceImpl
import com.scrap2025.scrap2025.data.remote.datasource.UserRemoteDataSource
import com.scrap2025.scrap2025.data.remote.datasource.UserRemoteDataSourceImpl
import com.scrap2025.scrap2025.model.enums.SnsType
import com.scrap2025.scrap2025.repository.AuthRepository
import com.scrap2025.scrap2025.repository.AuthRepositoryImpl
import com.scrap2025.scrap2025.repository.CategoryRepository
import com.scrap2025.scrap2025.repository.CategoryRepositoryImpl
import com.scrap2025.scrap2025.repository.LinkPreviewRepository
import com.scrap2025.scrap2025.repository.LinkPreviewRepositoryImpl
import com.scrap2025.scrap2025.repository.MyPageRepository
import com.scrap2025.scrap2025.repository.MyPageRepositoryImpl
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
    abstract fun bindScrapRepository(scrapRepositoryImpl: ScrapRepositoryImpl): ScrapRepository

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
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindMyPageRepository(myPageRepositoryImpl: MyPageRepositoryImpl): MyPageRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRemoteDataSource(
        categoryRemoteDataSourceImpl: CategoryRemoteDataSourceImpl
    ): CategoryRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindScrapRemoteDataSource(
        scrapRemoteDataSourceImpl: ScrapRemoteDataSourceImpl
    ): ScrapRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(
        authRemoteDataSourceImpl: AuthRemoteDataSourceImpl
    ): AuthRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindUserRemoteDataSource(
        userRemoteDataSourceImpl: UserRemoteDataSourceImpl
    ): UserRemoteDataSource

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
    abstract fun bindGoogleLoginProvider(
        googleLoginProvider: GoogleLoginProvider
    ): SocialLoginProvider

    companion object {
        @Provides
        @Singleton
        fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager =
            PreferencesManager(context)
    }
}

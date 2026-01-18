package com.scrap2025.scrap2025

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NidOAuth
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ScrapApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initNaverLogin()
        initKakaoLogin()
    }

    private fun initNaverLogin() {
        val id = BuildConfig.NAVER_CLIENT_ID
        val secret = BuildConfig.NAVER_CLIENT_SECRET
        val name = BuildConfig.NAVER_CLIENT_NAME

        NidOAuth.initialize(
            context = this,
            clientId = id,
            clientSecret = secret,
            clientName = name
        )
    }

    private fun initKakaoLogin() {
        val nativeAppKey = BuildConfig.KAKAO_NATIVE_APP_KEY

        KakaoSdk.init(this, nativeAppKey)
    }
}

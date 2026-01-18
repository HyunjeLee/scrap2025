package com.scrap2025.scrap2025

import android.app.Application
import android.util.Log
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

        // 모든 값이 있고, "null"이라는 문자열이 아닐 때만 실행
        if (listOf(id, secret, name).all { it.isSafe() }) {
            NidOAuth.initialize(
                context = this,
                clientId = id,
                clientSecret = secret,
                clientName = name
            )
        } else {
            Log.e("ScrapApplication", "Naver Login configuration is missing or invalid.")
        }
    }

    private fun initKakaoLogin() {
        val nativeAppKey = BuildConfig.KAKAO_NATIVE_APP_KEY

        KakaoSdk.init(this, nativeAppKey)
    }

    // "null" 문자열 체크와 비어있는지 체크를 하나로 묶은 확장 함수
    private fun String.isSafe() = isNotEmpty() && this != "null"
}

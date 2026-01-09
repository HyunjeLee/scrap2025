package com.scrap2025.scrap2025

import android.app.Application
import com.navercorp.nid.NidOAuth
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ScrapApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 네이버 로그인 SDK 초기화
        // BuildConfig를 통해 local.properties에서 읽어온 값 사용
        NidOAuth.initialize(
            context = this,
            clientId = BuildConfig.NAVER_CLIENT_ID,
            clientSecret = BuildConfig.NAVER_CLIENT_SECRET,
            clientName = BuildConfig.NAVER_CLIENT_NAME
        )
    }

// LOCAL-FIRST -> SERVER-FIRST
//    override val workManagerConfiguration: Configuration
//        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()
}

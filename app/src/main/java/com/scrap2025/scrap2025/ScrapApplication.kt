package com.scrap2025.scrap2025

import android.app.Application
import com.navercorp.nid.NidOAuth
import com.scrap2025.scrap2025.data.local.DatabaseInitializer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class ScrapApplication : Application() {

    @Inject
    lateinit var databaseInitializer: DatabaseInitializer

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

        // 초기 데이터 설정
        CoroutineScope(Dispatchers.IO).launch {
            databaseInitializer.init()
        }
    }
}

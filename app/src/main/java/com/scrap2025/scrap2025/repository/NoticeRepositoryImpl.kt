package com.scrap2025.scrap2025.repository

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.scrap2025.scrap2025.model.NoticeConfig
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json

class NoticeRepositoryImpl
@Inject constructor(private val remoteConfig: FirebaseRemoteConfig) :
    NoticeRepository {

    override suspend fun getNotice(): NoticeConfig? = try {
        remoteConfig.awaitFetchAndActivate()

        val raw = remoteConfig.getString(NOTICE_CONFIG_KEY)
        raw.takeIf { it.isNotBlank() }?.let { json.decodeFromString<NoticeConfig>(it) }
            ?.takeIf { it.enabled && it.id.isNotBlank() }
    } catch (e: Exception) {
        Log.w(TAG, "Failed to load notice config", e)
        null
    }

    private suspend fun FirebaseRemoteConfig.awaitFetchAndActivate(): Boolean =
        suspendCancellableCoroutine { continuation ->
            fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(task.result)
                } else {
                    continuation.resumeWithException(
                        task.exception ?: IllegalStateException("Remote Config fetch failed")
                    )
                }
            }
        }

    companion object {
        private const val TAG = "NoticeRepository"
        private const val NOTICE_CONFIG_KEY = "notice_config"
        private val json = Json { ignoreUnknownKeys = true }
    }
}

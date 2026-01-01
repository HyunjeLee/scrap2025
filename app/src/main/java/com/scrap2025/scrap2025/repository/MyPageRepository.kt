package com.scrap2025.scrap2025.repository

import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.scrap2025.scrap2025.data.local.dao.MyPageDao
import com.scrap2025.scrap2025.data.local.entity.MyPageEntity
import com.scrap2025.scrap2025.data.model.SyncStatus
import com.scrap2025.scrap2025.data.remote.AuthService
import com.scrap2025.scrap2025.data.remote.dto.MemberInfo
import com.scrap2025.scrap2025.data.remote.dto.MyPageResponse
import com.scrap2025.scrap2025.data.remote.dto.Statistics
import com.scrap2025.scrap2025.worker.MyPageSyncWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MyPageRepository
@Inject
constructor(
    private val myPageDao: MyPageDao,
    private val authService: AuthService,
    private val workManager: WorkManager
) {

    // 1. UI Observes this (SSOT)
    // Convert Entity to Domain Model (MyPageResponse)
    // If Entity is null, emit null (or handle default state)
    val myPageData: Flow<MyPageResponse?> =
        myPageDao.getMyPage().map { entity -> entity?.toDomainModel() }

    // 2. Triggered by ViewModel on init or pull-to-refresh
    // Returns true if sync successful, false otherwise.
    suspend fun invokeMyPageSync(): Boolean {
        return try {
            val response = authService.getMyPage()
            if (response.isSuccessful) {
                response.body()?.result?.let { remoteData ->
                    // Save to Local DB -> Triggers myPageData Flow -> UI Updates automatically
                    myPageDao.insertMyPage(remoteData.toEntity())
                }
                true
            } else {
                Log.e("MyPageRepository", "Sync failed: ${response.code()}")
                markAsPending()
                false
            }
        } catch (e: Exception) {
            Log.e("MyPageRepository", "Sync error", e)
            markAsPending()
            throw e // Re-throw to allow Worker to retry
        }
    }

    private suspend fun markAsPending() {
        val currentEntity = myPageDao.getMyPage().firstOrNull()
        if (currentEntity != null) {
            myPageDao.insertMyPage(currentEntity.copy(syncStatus = SyncStatus.PENDING))

            // Enqueue WorkManager for background sync
            val constraints =
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

            val syncRequest =
                OneTimeWorkRequestBuilder<MyPageSyncWorker>()
                    .setConstraints(constraints)
                    .build()

            workManager.enqueue(syncRequest)
            Log.d("MyPageRepository", "Enqueued OneTimeWorkRequest for MyPage sync")
        }
    }

    private fun MyPageEntity.toDomainModel(): MyPageResponse {
        return MyPageResponse(
            memberInfo = MemberInfo(name = this.name),
            statistics =
                Statistics(
                    totalCategory = this.totalCategory,
                    totalScrap = this.totalScrap
                )
        )
    }

    private fun MyPageResponse.toEntity(): MyPageEntity {
        return MyPageEntity(
            name = this.memberInfo.name,
            totalCategory = this.statistics.totalCategory,
            totalScrap = this.statistics.totalScrap,
            syncStatus = SyncStatus.SYNCED
        )
    }
}

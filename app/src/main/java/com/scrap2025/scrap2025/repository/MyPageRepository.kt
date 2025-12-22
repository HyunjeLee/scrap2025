package com.scrap2025.scrap2025.repository

import android.util.Log
import com.scrap2025.scrap2025.data.local.dao.MyPageDao
import com.scrap2025.scrap2025.data.local.entity.MyPageEntity
import com.scrap2025.scrap2025.data.model.MyPageResult
import com.scrap2025.scrap2025.data.model.SyncStatus
import com.scrap2025.scrap2025.data.remote.AuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MyPageRepository
@Inject
constructor(private val myPageDao: MyPageDao, private val authService: AuthService) {

    // 1. UI Observes this (SSOT)
    // Convert Entity to Domain Model (MyPageResult)
    // If Entity is null, emit null (or handle default state)
    val myPageData: Flow<MyPageResult?> =
        myPageDao.getMyPage().map { entity -> entity?.toDomainModel() }

    // 2. Triggered by ViewModel on init or pull-to-refresh
    suspend fun invokeMyPageSync(token: String) {
        try {
            val response = authService.getMyPage(token)
            if (response.isSuccessful) {
                response.body()?.result?.let { remoteData ->
                    // Save to Local DB -> Triggers myPageData Flow -> UI Updates automatically
                    myPageDao.insertMyPage(remoteData.toEntity())
                }
            } else {
                Log.e("MyPageRepository", "Sync failed: ${response.code()}")
                markAsPending()
            }
        } catch (e: Exception) {
            Log.e("MyPageRepository", "Sync error", e)
            markAsPending()
        }
    }

    private suspend fun markAsPending() {
        val currentEntity = myPageDao.getMyPage().firstOrNull()
        if (currentEntity != null) {
            myPageDao.insertMyPage(currentEntity.copy(syncStatus = SyncStatus.PENDING))
        }
    }

    private fun MyPageEntity.toDomainModel(): MyPageResult {
        return MyPageResult(
            memberInfo = com.scrap2025.scrap2025.data.model.MemberInfo(name = this.name),
            statistics =
                com.scrap2025.scrap2025.data.model.Statistics(
                    totalCategory = this.totalCategory,
                    totalScrap = this.totalScrap
                )
        )
    }

    private fun MyPageResult.toEntity(): MyPageEntity {
        return MyPageEntity(
            name = this.memberInfo.name,
            totalCategory = this.statistics.totalCategory,
            totalScrap = this.statistics.totalScrap,
            syncStatus = SyncStatus.SYNCED
        )
    }
}

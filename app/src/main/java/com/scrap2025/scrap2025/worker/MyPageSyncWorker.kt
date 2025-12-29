package com.scrap2025.scrap2025.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.scrap2025.scrap2025.repository.MyPageRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MyPageSyncWorker
@AssistedInject
constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val myPageRepository: MyPageRepository,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("MyPageSyncWorker", "Starting background sync for MyPage")

        return try {
            // invokeMyPageSync가 Exception을 던지면 catch 블록으로 이동하여 retry()
            // false를 반환하면(서버 4xx/5xx 등) -> retry() 할지 failure() 할지 결정
            val isSuccess = myPageRepository.invokeMyPageSync()

            if (isSuccess) {
                Result.success()
            } else {
                // 일시적인 서버 오류일 수 있으므로 재시도
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e("MyPageSyncWorker", "Sync failed with exception", e)
            Result.retry()
        }
    }
}

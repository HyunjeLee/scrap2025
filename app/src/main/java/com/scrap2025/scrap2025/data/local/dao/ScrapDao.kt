package com.scrap2025.scrap2025.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.scrap2025.scrap2025.data.local.entity.ScrapEntity
import com.scrap2025.scrap2025.data.model.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ScrapDao {
    @Query("SELECT * FROM scraps")
    fun getAllScraps(): Flow<List<ScrapEntity>>

    @Query("SELECT * FROM scraps WHERE categoryId = :categoryId")
    fun getAllScrapsByCategoryId(categoryId: String): Flow<List<ScrapEntity>>

    @Query("SELECT * FROM scraps WHERE id = :id")
    suspend fun getScrapById(id: String): ScrapEntity? // 추후 대규모 리팩토링 시 해당 함수 삭제 후 아래 함수로 변경할 것

    @Query("SELECT * FROM scraps WHERE id = :id")
    fun getScrapByIdFlow(id: String): Flow<ScrapEntity?> // Flow로 변경 (suspend 제거)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScrap(scrap: ScrapEntity)

    @Query("DELETE FROM scraps WHERE id = :id")
    suspend fun deleteScrap(id: String)

    @Query("UPDATE scraps SET memo = :memo WHERE id = :id")
    suspend fun updateScrapMemo(id: String, memo: String?)

    @Query("UPDATE scraps SET categoryId = :categoryId WHERE id = :id")
    suspend fun moveScrap(id: String, categoryId: String)

    @Query("UPDATE scraps SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateIsFavorite(id: String, isFavorite: Boolean)

    @Query("UPDATE scraps SET categoryId = :targetId WHERE categoryId = :sourceId")
    suspend fun moveScraps(sourceId: String, targetId: String): Int

    @Query("SELECT COUNT(*) FROM scraps")
    fun getScrapCount(): Flow<Int>

    @Upsert
    suspend fun upsertScraps(scraps: List<ScrapEntity>)

    @Query("UPDATE scraps SET remoteId = :remoteId, isFavorite = :isFavorite, syncStatus = :status WHERE id = :id")
    suspend fun updateScrapRemoteId(id: String, remoteId: Int, isFavorite: Boolean, status: SyncStatus)

    @Query("UPDATE scraps SET description = :description, memo = :memo WHERE id = :id")
    suspend fun updateScrapDetails(id: String, description: String, memo: String?)

    @Query("SELECT * FROM scraps WHERE remoteId = :remoteId")
    suspend fun getScrapByRemoteId(remoteId: Int): ScrapEntity?
}

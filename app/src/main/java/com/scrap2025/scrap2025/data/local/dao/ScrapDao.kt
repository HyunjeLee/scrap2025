package com.scrap2025.scrap2025.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.scrap2025.scrap2025.data.local.entity.ScrapEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScrapDao {
    @Query("SELECT * FROM scraps")
    fun getAllScraps(): Flow<List<ScrapEntity>>

    @Query("SELECT * FROM scraps WHERE categoryId = :categoryId")
    fun getAllScrapsByCategoryId(categoryId: Long): Flow<List<ScrapEntity>>

    @Query("SELECT * FROM scraps WHERE id = :id")
    suspend fun getScrapById(id: Long): ScrapEntity?

    @Query("SELECT * FROM scraps WHERE id = :id")
    fun getScrapByIdFlow(id: Long): Flow<ScrapEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScrap(scrap: ScrapEntity)

    @Query("DELETE FROM scraps WHERE id = :id")
    suspend fun deleteScrap(id: Long)

    @Query("UPDATE scraps SET memo = :memo WHERE id = :id")
    suspend fun updateScrapMemo(id: Long, memo: String?)

    @Query("UPDATE scraps SET categoryId = :categoryId WHERE id = :id")
    suspend fun moveScrap(id: Long, categoryId: Long)

    @Query("UPDATE scraps SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateIsFavorite(id: Long, isFavorite: Boolean)

    @Query("UPDATE scraps SET categoryId = :targetId WHERE categoryId = :sourceId")
    suspend fun moveScraps(sourceId: Long, targetId: Long): Int

    @Query("SELECT COUNT(*) FROM scraps")
    fun getScrapCount(): Flow<Int>

    @Upsert
    suspend fun upsertScraps(scraps: List<ScrapEntity>)

    @Query("UPDATE scraps SET description = :description, memo = :memo WHERE id = :id")
    suspend fun updateScrapDetails(id: Long, description: String, memo: String?)

    @Query("SELECT * FROM scraps WHERE id = :id")
    suspend fun getScrapByRemoteId(id: Long): ScrapEntity?

    @Query("DELETE FROM scraps")
    suspend fun deleteAll()
}

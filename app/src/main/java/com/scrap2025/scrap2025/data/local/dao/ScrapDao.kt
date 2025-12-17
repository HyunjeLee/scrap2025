package com.scrap2025.scrap2025.data.local.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scrap2025.scrap2025.data.local.entity.ScrapEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScrapDao {
    @Query("SELECT * FROM scraps")
    fun getAllScraps(): Flow<List<ScrapEntity>>

    @Query("SELECT * FROM scraps WHERE categoryId = :categoryId")
    fun getScrapsByCategoryId(categoryId: String): Flow<List<ScrapEntity>>

    @Query("SELECT * FROM scraps WHERE id = :id")
    suspend fun getScrapById(id: String): ScrapEntity?

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
}

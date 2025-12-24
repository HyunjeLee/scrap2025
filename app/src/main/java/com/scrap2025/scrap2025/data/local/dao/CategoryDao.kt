package com.scrap2025.scrap2025.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.scrap2025.scrap2025.data.local.entity.CategoryEntity
import com.scrap2025.scrap2025.data.model.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY orderIndex ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT COUNT(*) FROM categories")
    fun getCategoryCount(): Flow<Int>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: String): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Upsert
    suspend fun upsertCategories(categories: List<CategoryEntity>)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategory(id: String)

    @Query("UPDATE categories SET name = :name WHERE id = :id")
    suspend fun updateCategoryName(id: String, name: String)

    @Query("UPDATE categories SET scrapCount = scrapCount + 1 WHERE id = :id")
    suspend fun incrementScrapCount(id: String)

    @Query("UPDATE categories SET scrapCount = scrapCount - 1 WHERE id = :id AND scrapCount > 0")
    suspend fun decrementScrapCount(id: String)

    @Query("UPDATE categories SET scrapCount = scrapCount + :amount WHERE id = :id")
    suspend fun updateScrapCount(id: String, amount: Int)

    @Query("SELECT * FROM categories")
    suspend fun getAllCategoriesSnapshot(): List<CategoryEntity>

    @Query("UPDATE categories SET remoteId = :remoteId, syncStatus = :status WHERE id = :id")
    suspend fun updateCategoryRemoteId(id: String, remoteId: Int, status: SyncStatus)

    @Query("UPDATE categories SET syncStatus = :status WHERE id = :id")
    suspend fun updateCategoryStatus(id: String, status: SyncStatus)

    @Query("SELECT * FROM categories WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultCategory(): CategoryEntity?
}

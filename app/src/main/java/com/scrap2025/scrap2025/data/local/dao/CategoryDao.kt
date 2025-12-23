package com.scrap2025.scrap2025.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scrap2025.scrap2025.data.local.entity.CategoryEntity
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

    // 리스트 전체 업데이트 (순서 변경 시 사용)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

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

    // 순서 변경을 위한 개별 업데이트
    @Query("UPDATE categories SET orderIndex = :orderIndex WHERE id = :id")
    suspend fun updateCategoryOrder(id: String, orderIndex: Int)
}

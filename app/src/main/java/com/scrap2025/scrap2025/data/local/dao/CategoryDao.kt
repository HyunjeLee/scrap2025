package com.scrap2025.scrap2025.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scrap2025.scrap2025.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: String): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

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
}

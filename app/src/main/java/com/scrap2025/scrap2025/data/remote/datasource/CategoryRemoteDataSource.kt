package com.scrap2025.scrap2025.data.remote.datasource

import com.scrap2025.scrap2025.data.remote.dto.CategoryListResponse
import com.scrap2025.scrap2025.data.remote.dto.RenameCategoryResponse
import com.scrap2025.scrap2025.data.remote.dto.SequenceCategoryResponse

interface CategoryRemoteDataSource {
    suspend fun getCategories(): CategoryListResponse

    suspend fun createCategory(title: String)

    suspend fun renameCategory(categoryId: Long, newTitle: String): RenameCategoryResponse

    suspend fun deleteCategory(categoryId: Long)

    suspend fun updateCategorySequence(categoryIds: List<Long>): SequenceCategoryResponse
}

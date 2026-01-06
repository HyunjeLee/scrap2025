package com.scrap2025.scrap2025.data.remote.datasource

import com.scrap2025.scrap2025.data.remote.dto.CategoryListResponse
import com.scrap2025.scrap2025.data.remote.dto.CreateCategoryResponse
import com.scrap2025.scrap2025.data.remote.dto.RenameCategoryResponse
import com.scrap2025.scrap2025.data.remote.dto.SequenceCategoryResponse

interface CategoryRemoteDataSource {
    suspend fun getCategories(): CategoryListResponse
    suspend fun createCategory(title: String): CreateCategoryResponse
    suspend fun renameCategory(categoryId: Int, newTitle: String): RenameCategoryResponse
    suspend fun deleteCategory(categoryId: Int)
    suspend fun updateCategorySequence(categoryIdList: List<Int>): SequenceCategoryResponse
}

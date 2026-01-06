package com.scrap2025.scrap2025.data.remote.datasource

import com.scrap2025.scrap2025.data.remote.dto.CategoryListResponse
import com.scrap2025.scrap2025.data.remote.dto.CreateCategoryResponse
import com.scrap2025.scrap2025.data.remote.dto.RenameCategoryResponse
import com.scrap2025.scrap2025.data.remote.dto.SequenceCategoryResponse
import com.scrap2025.scrap2025.model.Result

interface CategoryRemoteDataSource {
    suspend fun getCategories(): Result<CategoryListResponse>
    suspend fun createCategory(title: String): Result<CreateCategoryResponse>
    suspend fun renameCategory(categoryId: Int, newTitle: String): Result<RenameCategoryResponse>
    suspend fun deleteCategory(categoryId: Int): Result<Unit>
    suspend fun updateCategorySequence(categoryIdList: List<Int>): Result<SequenceCategoryResponse>
}

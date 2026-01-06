package com.scrap2025.scrap2025.data.remote.datasource

import com.scrap2025.scrap2025.data.remote.api.CategoryService
import com.scrap2025.scrap2025.data.remote.dto.CategoryListResponse
import com.scrap2025.scrap2025.data.remote.dto.CreateCategoryRequest
import com.scrap2025.scrap2025.data.remote.dto.CreateCategoryResponse
import com.scrap2025.scrap2025.data.remote.dto.RenameCategoryRequest
import com.scrap2025.scrap2025.data.remote.dto.RenameCategoryResponse
import com.scrap2025.scrap2025.data.remote.dto.SequenceCategoryRequest
import com.scrap2025.scrap2025.data.remote.dto.SequenceCategoryResponse
import javax.inject.Inject

class CategoryRemoteDataSourceImpl
@Inject constructor(private val categoryService: CategoryService) : CategoryRemoteDataSource {

    override suspend fun getCategories(): CategoryListResponse {
        val response = categoryService.getCategories()
        if (response.isSuccessful) {
            return response.body()?.result ?: throw Exception("Response body is null")
        } else {
            throw Exception("Get Categories failed code: ${response.code()}")
        }
    }

    override suspend fun createCategory(title: String): CreateCategoryResponse {
        val request = CreateCategoryRequest(categoryTitle = title)
        val response = categoryService.createCategory(request)
        if (response.isSuccessful) {
            return response.body()?.result ?: throw Exception("Response body is null")
        } else {
            throw Exception("Create failed code: ${response.code()}")
        }
    }

    override suspend fun renameCategory(
        categoryId: Int, newTitle: String
    ): RenameCategoryResponse {
        val request = RenameCategoryRequest(newCategoryTitle = newTitle)
        val response = categoryService.renameCategory(categoryId, request)
        if (response.isSuccessful) {
            return response.body()?.result ?: throw Exception("Response body is null")
        } else {
            throw Exception("Rename failed code: ${response.code()}")
        }
    }

    override suspend fun deleteCategory(categoryId: Int) {
        val response = categoryService.deleteCategory(categoryId)
        if (!response.isSuccessful) {
            throw Exception("Delete failed code: ${response.code()}")
        }
    }

    override suspend fun updateCategorySequence(
        categoryIdList: List<Int>
    ): SequenceCategoryResponse {
        val request = SequenceCategoryRequest(categoryIdList = categoryIdList)
        val response = categoryService.updateCategorySequence(request)
        if (response.isSuccessful) {
            return response.body()?.result ?: throw Exception("Response body is null")
        } else {
            throw Exception("Reorder failed code: ${response.code()}")
        }
    }
}

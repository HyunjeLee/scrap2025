package com.scrap2025.scrap2025.data.remote.api

import com.scrap2025.scrap2025.data.remote.dto.BaseResponse
import com.scrap2025.scrap2025.data.remote.dto.CategoryListResponse
import com.scrap2025.scrap2025.data.remote.dto.CreateCategoryRequest
import com.scrap2025.scrap2025.data.remote.dto.RenameCategoryRequest
import com.scrap2025.scrap2025.data.remote.dto.RenameCategoryResponse
import com.scrap2025.scrap2025.data.remote.dto.SequenceCategoryRequest
import com.scrap2025.scrap2025.data.remote.dto.SequenceCategoryResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CategoryService {
    @GET("/auth/categories")
    suspend fun getCategories(): Response<BaseResponse<CategoryListResponse>>

    @POST("/auth/categories")
    suspend fun createCategory(
        @Body body: CreateCategoryRequest
    ): Response<BaseResponse<JsonElement?>>

    @PATCH("/auth/categories/{categoryId}/title")
    suspend fun renameCategory(
        @Path("categoryId") categoryId: Long,
        @Body body: RenameCategoryRequest
    ): Response<BaseResponse<RenameCategoryResponse>>

    @DELETE("/auth/categories/{category-id}")
    suspend fun deleteCategory(@Path("category-id") categoryId: Long): Response<BaseResponse<Unit?>>

    @PATCH("/auth/categories/sequence")
    suspend fun updateCategorySequence(
        @Body body: SequenceCategoryRequest
    ): Response<BaseResponse<SequenceCategoryResponse>>
}

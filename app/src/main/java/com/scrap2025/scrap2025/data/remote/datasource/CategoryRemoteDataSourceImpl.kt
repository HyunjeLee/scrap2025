package com.scrap2025.scrap2025.data.remote.datasource

import com.scrap2025.scrap2025.data.remote.api.CategoryService
import com.scrap2025.scrap2025.data.remote.dto.CategoryListResponse
import com.scrap2025.scrap2025.data.remote.dto.CreateCategoryRequest
import com.scrap2025.scrap2025.data.remote.dto.CreateCategoryResponse
import com.scrap2025.scrap2025.data.remote.dto.RenameCategoryRequest
import com.scrap2025.scrap2025.data.remote.dto.RenameCategoryResponse
import com.scrap2025.scrap2025.data.remote.dto.SequenceCategoryRequest
import com.scrap2025.scrap2025.data.remote.dto.SequenceCategoryResponse
import com.scrap2025.scrap2025.model.Result
import javax.inject.Inject

class CategoryRemoteDataSourceImpl
@Inject constructor(private val categoryService: CategoryService) : CategoryRemoteDataSource {

    override suspend fun getCategories(): Result<CategoryListResponse> {
        return try {
            val response = categoryService.getCategories()
            if (response.isSuccessful) {
                val body = response.body()?.result
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error(Exception("Response body is null"), "카테고리 목록 응답 오류")
                }
            } else {
                Result.Error(
                    Exception("Get Categories failed code: ${response.code()}"), "카테고리 목록 조회 실패"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "카테고리 목록 조회 중 오류 발생")
        }
    }

    override suspend fun createCategory(title: String): Result<CreateCategoryResponse> {
        return try {
            val request = CreateCategoryRequest(categoryTitle = title)
            val response = categoryService.createCategory(request)
            if (response.isSuccessful) {
                val body = response.body()?.result
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error(Exception("Response body is null"), "카테고리 생성 응답 오류")
                }
            } else {
                Result.Error(Exception("Create failed code: ${response.code()}"), "카테고리 생성 실패")
            }
        } catch (e: Exception) {
            Result.Error(e, "카테고리 생성 중 오류 발생")
        }
    }

    override suspend fun renameCategory(
        categoryId: Int, newTitle: String
    ): Result<RenameCategoryResponse> {
        return try {
            val request = RenameCategoryRequest(newCategoryTitle = newTitle)
            val response = categoryService.renameCategory(categoryId, request)
            if (response.isSuccessful) {
                val body = response.body()?.result
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error(Exception("Response body is null"), "카테고리 이름 수정 응답 오류")
                }
            } else {
                Result.Error(Exception("Rename failed code: ${response.code()}"), "카테고리 이름 수정 실패")
            }
        } catch (e: Exception) {
            Result.Error(e, "카테고리 이름 수정 중 오류 발생")
        }
    }

    override suspend fun deleteCategory(categoryId: Int): Result<Unit> {
        return try {
            val response = categoryService.deleteCategory(categoryId)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Delete failed code: ${response.code()}"), "카테고리 삭제 실패")
            }
        } catch (e: Exception) {
            Result.Error(e, "카테고리 삭제 중 오류 발생")
        }
    }

    override suspend fun updateCategorySequence(
        categoryIdList: List<Int>
    ): Result<SequenceCategoryResponse> {
        return try {
            val request = SequenceCategoryRequest(categoryIdList = categoryIdList)
            val response = categoryService.updateCategorySequence(request)
            if (response.isSuccessful) {
                val body = response.body()?.result
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error(Exception("Response body is null"), "카테고리 순서 변경 응답 오류")
                }
            } else {
                Result.Error(Exception("Reorder failed code: ${response.code()}"), "카테고리 순서 변경 실패")
            }
        } catch (e: Exception) {
            Result.Error(e, "카테고리 순서 변경 중 오류 발생")
        }
    }
}

package com.scrap2025.scrap2025.repository

import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.model.Result
import kotlinx.coroutines.flow.Flow

/** CategoryRepository - 카테고리 데이터 접근 추상화 인터페이스 데이터 소스(로컬/리모트)에 독립적인 비즈니스 로직 제공 */
interface CategoryRepository {
    /**
     * 전체 카테고리 목록 조회
     * @return Flow로 감싸진 Result<List<CategoryItem>>
     */
    fun getCategories(): Flow<Result<List<CategoryItem>>>

    /**
     * 특정 카테고리 조회
     * @param id 카테고리 ID
     * @return Result<CategoryItem>
     */
    suspend fun getCategoryById(id: String): Result<CategoryItem>

    /**
     * 새로운 카테고리 추가
     * @param item 추가할 CategoryItem
     * @return Result<Unit> (성공/실패)
     */
    suspend fun addCategory(item: CategoryItem): Result<Unit>

    /**
     * 카테고리 삭제
     * @param id 삭제할 카테고리 ID
     * @return Result<Unit> (성공/실패)
     */
    suspend fun deleteCategory(id: String): Result<Unit>

    /**
     * 카테고리 정보 업데이트
     * @param id 업데이트할 카테고리 ID
     * @param name 새로운 카테고리명
     * @return Result<Unit> (성공/실패)
     */
    suspend fun updateCategory(id: String, name: String): Result<Unit>

    /**
     * 전체 카테고리 개수 조회
     * @return Flow<Int>
     */
    fun getCategoryCount(): Flow<Int>

    /**
     * 카테고리 동기화 (Remote -> Local)
     * @param token API Access Token
     * @return Result<Unit>
     */
    suspend fun syncCategories(token: String): Result<Unit>
}

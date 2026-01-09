package com.scrap2025.scrap2025.repository

import com.scrap2025.scrap2025.model.CategoryItem
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/** CategoryRepository - 카테고리 데이터 접근 추상화 인터페이스 데이터 소스(로컬/리모트)에 독립적인 비즈니스 로직 제공 */
interface CategoryRepository {

    /**
     * categoryCount 조회 및 관찰 목적 event
     */
    val refreshEvent: SharedFlow<Unit>

    val allCategories: StateFlow<Result<List<CategoryItem>>>
    val defaultCategory: CategoryItem?
    val selectedCategoryId: StateFlow<Long?>
    val selectedCategoryTitle: StateFlow<String?>

    suspend fun refreshCategories()
    fun selectCategory(id: Long, title: String)

    /**
     * 새로운 카테고리 추가
     * @param item 추가할 CategoryItem
     * @return Result<Unit> (성공/실패)
     */
    suspend fun createCategory(title: String): Result<Unit>

    /**
     * 카테고리 삭제
     * @param id 삭제할 카테고리 ID
     * @return Result<Unit> (성공/실패)
     */
    suspend fun deleteCategory(id: Long): Result<Unit>

    /**
     * 카테고리 정보 업데이트
     * @param id 업데이트할 카테고리 ID
     * @param name 새로운 카테고리명
     * @return Result<Unit> (성공/실패)
     */
    suspend fun updateCategory(id: Long, newTitle: String): Result<Unit>

    /**
     * 카테고리 순서 변경
     * @param ids 순서가 변경된 categoryId 리스트
     * @return Result<Unit>
     */
    suspend fun reorderCategories(ids: List<Long>): Result<Unit>
}

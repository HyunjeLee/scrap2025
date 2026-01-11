package com.scrap2025.scrap2025.repository

import com.scrap2025.scrap2025.model.CategoryItem
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/** CategoryRepository - 카테고리 데이터 접근을 추상화하는 인터페이스. 데이터 소스(로컬/원격)에 독립적인 비즈니스 로직을 제공합니다. */
interface CategoryRepository {

    /** 카테고리 목록의 변경 사항(추가, 삭제, 수정 등)을 알리기 위한 공유 흐름 */
    val refreshEvent: SharedFlow<Unit>

    /** 모든 카테고리 목록을 담고 있는 상태 흐름 */
    val allCategories: StateFlow<Result<List<CategoryItem>>>

    /** 기본 카테고리 (수정/삭제 불가한 기본 제공 카테고리) */
    val defaultCategory: CategoryItem?

    /** 전역적으로 현재 선택된 카테고리 ID */
    val selectedCategoryId: StateFlow<Long?>

    /** 전역적으로 현재 선택된 카테고리 제목 */
    val selectedCategoryTitle: StateFlow<String?>

    /** 최신 카테고리 데이터를 원격에서 가져와 갱신 */
    suspend fun refreshCategories()

    /** 전역적으로 선택된 카테고리를 설정 */
    fun setGlobalCategory(id: Long, title: String)

    /**
     * 새로운 카테고리 추가
     * @param title 추가할 카테고리 제목
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
     * @param newTitle 새로운 카테고리명
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

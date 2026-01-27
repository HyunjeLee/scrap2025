package com.scrap2025.scrap2025.viewmodel

import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.repository.CategoryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryViewModelTest {

    private lateinit var viewModel: CategoryViewModel
    private lateinit var categoryRepository: CategoryRepository

    // Mock Flows
    private val allCategoriesFlow =
        MutableStateFlow<Result<List<CategoryItem>>>(Result.success(emptyList()))
    private val refreshEventFlow = MutableSharedFlow<Unit>()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        categoryRepository = mockk(relaxed = true)
        every { categoryRepository.allCategories } returns allCategoriesFlow
        every { categoryRepository.refreshEvent } returns refreshEventFlow
        coEvery { categoryRepository.refreshCategories() } returns Unit

        viewModel = CategoryViewModel(categoryRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun refresh_triggers_categoryRepository_refreshCategories() = runTest(testDispatcher) {
        // [Given] init 블록에서 이미 1회 호출됨

        // [When] 리프레시 호출
        viewModel.refresh()

        // 코루틴 실행 대기
        testDispatcher.scheduler.advanceUntilIdle()

        // [Then] 총 2회 호출 확인 (init 1회 + refresh 1회)
        coVerify(exactly = 2) { categoryRepository.refreshCategories() }
    }
}

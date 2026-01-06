package com.scrap2025.scrap2025.ui.category.screens

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scrap2025.scrap2025.data.model.SyncStatus
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.ui.category.components.CategoryItemCard
import com.scrap2025.scrap2025.ui.common.components.ErrorScreen
import com.scrap2025.scrap2025.ui.theme.BackgroundColor
import com.scrap2025.scrap2025.ui.theme.GrayColor
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.viewmodel.CategoryUiState
import com.scrap2025.scrap2025.viewmodel.CategoryViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

/** CategoryScreen - Container Composable ViewModel에서 상태를 추출하여 CategoryScreenContent에 전달 */
@Composable
fun CategoryScreen(
    onCategoryClick: (CategoryItem) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.categoryUiState.collectAsState()

    CategoryScreenContent(
        uiState = uiState,
        onCategoryClick = onCategoryClick,
        onAddClick = onAddClick,
        modifier = modifier,
        onMove = { from, to -> viewModel.moveCategory(from, to) },
        onDragStopped = { viewModel.updateCategoryOrder() })
}

/** CategoryScreenContent - Presentational Composable ViewModel 의존성 없이 순수한 데이터만 받아서 UI 렌더링 */
@Composable
fun CategoryScreenContent(
    uiState: CategoryUiState,
    onCategoryClick: (CategoryItem) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    onMove: (Int, Int) -> Unit = { _, _ -> },
    onDragStopped: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 헤더 (높이 68dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .background(Color.White),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "카테고리",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(start = 21.dp),
                )
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(), color = GrayColor, thickness = 0.5.dp
            )

            // 카테고리 리스트 - 상태 처리
            when (uiState) {
                is CategoryUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MainColorDeep)
                    }
                }

                is CategoryUiState.Error -> {
                    ErrorScreen(errorText = uiState.message ?: "카테고리를 불러올 수 없습니다.")
                }

                is CategoryUiState.Success -> {
                    // TODO: 전체 동기화 상태 표시 UI 추가 필요
                    // 동기화 상태 로깅
                    LaunchedEffect(uiState.categories) {
                        val hasPending =
                            uiState.categories.any { it.syncStatus == SyncStatus.PENDING }

                        if (hasPending) {
                            Log.d("CategorySync", "전체 카테고리 동기화 중... (Pending items detected)")
                        } else {
                            Log.d("CategorySync", "전체 카테고리 동기화 완료 (All items synced)")
                        }
                    }

                    // Creating LazyListState explicitly
                    val listState = rememberLazyListState()
                    val state = rememberReorderableLazyListState(
                        lazyListState = listState,
                        onMove = { from, to -> onMove(from.index, to.index) })

                    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                        items(items = uiState.categories, key = { it.id }) { item ->
                            ReorderableItem(state, key = item.id) { isDragging ->
                                val elevation = animateDpAsState(if (isDragging) 8.dp else 0.dp)

                                Column(
                                    modifier = Modifier
                                        .then(
                                            if (item.id != CategoryItem.DEFAULT_ID) {
                                                Modifier.draggableHandle(
                                                    onDragStopped = {
                                                        onDragStopped()
                                                    })
                                            } else {
                                                Modifier
                                            }
                                        )
                                        .shadow(elevation.value)
                                        .background(
                                            if (isDragging) Color.White
                                            else Color.Transparent
                                        )) {
                                    CategoryItemCard(
                                        categoryItem = item, onClick = { onCategoryClick(item) })
                                    HorizontalDivider(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = GrayColor,
                                        thickness = 0.5.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // FAB 버튼 - 오른쪽 하단에 고정
        FloatingActionButton(
            onClick = onAddClick,
            shape = CircleShape,
            containerColor = MainColor,
            contentColor = MainColorDeep,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 21.dp, bottom = 21.dp)
                .size(60.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "카테고리 추가",
                modifier = Modifier.size(50.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryScreenContentPreview() {

    val dummyCategories = listOf(
        CategoryItem(
            id = "1",
            name = "분류되지 않음",
            orderIndex = 0,
        ),
        CategoryItem(
            id = "2",
            name = "코테 자료",
            orderIndex = 0,
        ),
        CategoryItem(
            id = "3",
            name = "IBM Technology",
            orderIndex = 0,
        ),
    )

    Scrap2025Theme {
        CategoryScreenContent(
            uiState = CategoryUiState.Success(categories = dummyCategories),
            onCategoryClick = {},
            onAddClick = {})
    }
}

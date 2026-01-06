package com.scrap2025.scrap2025.ui.scrap.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scrap2025.scrap2025.model.enums.ViewMode
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import kotlinx.coroutines.launch

/** ScrapFloatingButtons - 화면 우측 하단에 위치하는 플로팅 버튼들 (맨 위로 가기, 스크랩 추가) */
@Composable
fun ScrapFloatingButtons(
    showScrollToTop: Boolean,
    viewMode: ViewMode,
    listState: LazyListState,
    gridState: LazyGridState,
    showAddScrapFab: Boolean,
    isSelectionMode: Boolean,
    onAddScrap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier.padding(end = 21.dp, bottom = 21.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 맨 위로 가기 버튼
        AnimatedVisibility(
            visible = showScrollToTop,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        when (viewMode) {
                            ViewMode.LIST -> {
                                listState.animateScrollToItem(0)
                                gridState.scrollToItem(0)
                            }

                            ViewMode.GRID -> {
                                gridState.animateScrollToItem(0)
                                listState.scrollToItem(0)
                            }
                        }
                    }
                },
                shape = CircleShape,
                containerColor = MainColor,
                contentColor = MainColorDeep,
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowUp,
                    contentDescription = "맨 위로가기",
                    tint = MainColorDeep,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        if (showAddScrapFab) {
            if (!isSelectionMode) { // 일반 모드일 때만 FAB 표시
                Spacer(Modifier.height(16.dp))

                // 스크랩 추가 버튼
                FloatingActionButton(
                    onClick = onAddScrap,
                    shape = CircleShape,
                    containerColor = MainColor,
                    contentColor = MainColorDeep,
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "스크랩 추가",
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
        }
    }
}

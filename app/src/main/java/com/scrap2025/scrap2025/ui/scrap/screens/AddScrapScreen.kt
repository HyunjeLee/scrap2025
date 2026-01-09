package com.scrap2025.scrap2025.ui.scrap.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.scrap2025.scrap2025.model.LinkPreview
import com.scrap2025.scrap2025.model.toScrapItem
import com.scrap2025.scrap2025.ui.scrap.components.ScrapItemCardList
import com.scrap2025.scrap2025.ui.theme.BackgroundColor
import com.scrap2025.scrap2025.ui.theme.DarkGrayColor
import com.scrap2025.scrap2025.ui.theme.GrayColor
import com.scrap2025.scrap2025.ui.theme.LightGrayColor
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.ui.theme.MainColorLight
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.viewmodel.AddScrapUiState
import com.scrap2025.scrap2025.viewmodel.AddScrapViewModel
import com.scrap2025.scrap2025.viewmodel.LinkPreviewUiState
import com.scrap2025.scrap2025.viewmodel.MainViewModel

/** AddScrapScreen - Container Composable ViewModel에서 상태를 추출하여 AddScrapScreenContent에 전달 */
@Composable
fun AddScrapScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddScrapViewModel = hiltViewModel(),
    mainViewModel: MainViewModel =
        hiltViewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)
) {
    val context = LocalContext.current
    val addScrapState by viewModel.addScrapState.collectAsState()
    val linkPreviewState by viewModel.linkPreviewUiState.collectAsState()
    val url by viewModel.url.collectAsState()
    val memo by viewModel.memo.collectAsState()
    val isLoading = addScrapState is AddScrapUiState.Loading
    val globalCategoryId by mainViewModel.selectedCategoryId.collectAsState()

    // 화면 진입 시 공유된 URL이 있는지 확인하고 소비
    LaunchedEffect(Unit) {
        val sharedUrl = mainViewModel.sharedUrl.value
        if (sharedUrl != null) {
            viewModel.updateUrl(sharedUrl)
            mainViewModel.setSharedUrl(null)
        }
    }

    // 스크랩 추가 상태 관찰
    LaunchedEffect(addScrapState) {
        when (val state = addScrapState) {
            is AddScrapUiState.Success -> {
                Toast.makeText(context, "스크랩이 추가되었습니다", Toast.LENGTH_SHORT).show()
                viewModel.resetState()
                onBack()
            }

            is AddScrapUiState.Error -> {
                Toast.makeText(context, state.message ?: "스크랩 추가 실패", Toast.LENGTH_SHORT).show()
                Log.e("AddScrapScreen", "Error: ${state.message}")
                viewModel.resetState()
            }

            else -> {}
        }
    }

    AddScrapScreenContent(
        isLoading = isLoading,
        linkPreviewUiState = linkPreviewState,
        url = url,
        memo = memo,
        onUrlChange = { viewModel.updateUrl(it) },
        onMemoChange = { viewModel.updateMemo(it) },
        onAddScrap = { currentUrl, currentMemo, linkPreview ->
            viewModel.addScrapItem(
                url = currentUrl,
                memo = currentMemo,
                linkPreview = linkPreview,
                categoryId = globalCategoryId ?: 0L
            )
        },
        onBack = { onBack() },
        modifier = modifier
    )
}

/** AddScrapScreenContent - Presentational Composable ViewModel 의존성 없이 순수한 데이터만 받아서 UI 렌더링 */
@Composable
fun AddScrapScreenContent(
    isLoading: Boolean,
    linkPreviewUiState: LinkPreviewUiState?,
    url: String,
    memo: String,
    onUrlChange: (String) -> Unit,
    onMemoChange: (String) -> Unit,
    onAddScrap: (url: String, memo: String, linkPreview: LinkPreview?) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // 링크 미리보기 데이터 추출
    val linkPreview =
        when (linkPreviewUiState) {
            is LinkPreviewUiState.Success -> linkPreviewUiState.preview
            else -> null
        }
    val isLoadingPreview = linkPreviewUiState is LinkPreviewUiState.Loading

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)) {
            // 톱바
            TopBar(onBackClick = onBack)

            Column(
                Modifier
                    .padding(vertical = 18.dp, horizontal = 16.dp)
                    .fillMaxWidth()
                    .background(color = MainColorLight, shape = RoundedCornerShape(8.dp))
                    .weight(1f)
            ) {

                // 링크 미리보기 영역
                if (isLoadingPreview) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            color = MainColorDeep
                        )
                    }
                } else if (linkPreview != null) {
                    Spacer(modifier = Modifier.height(16.dp))

                    ScrapItemCardList(
                        scrapItem = linkPreview.toScrapItem(),
                        isSelectionMode = false,
                        showCategory = false
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 링크 라벨
                Text(
                    text = "링크",
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium),
                    color = Color.Black,
                    modifier = Modifier.padding(start = 20.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 링크 입력 필드
                Box(modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()) {
                    TextField(
                        value = url,
                        onValueChange = { onUrlChange(it) },
                        placeholder = {
                            Text(
                                text = "링크를 입력하세요",
                                style =
                                    TextStyle(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                color = GrayColor
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors =
                            TextFieldDefaults.colors(
                                focusedContainerColor = MainColor,
                                unfocusedContainerColor = MainColor,
                                disabledContainerColor = MainColor,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                        textStyle =
                            TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            ),
                        singleLine = true,
                        enabled = !isLoading
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 메모 라벨
                Text(
                    text = "메모",
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium),
                    color = Color.Black,
                    modifier = Modifier.padding(start = 20.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 메모 입력 필드
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp)
                ) {
                    TextField(
                        value = memo,
                        onValueChange = { onMemoChange(it) },
                        placeholder = {
                            Text(
                                text = "메모를 입력하세요",
                                style =
                                    TextStyle(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                color = GrayColor
                            )
                        },
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(10.dp),
                        colors =
                            TextFieldDefaults.colors(
                                focusedContainerColor = MainColor,
                                unfocusedContainerColor = MainColor,
                                disabledContainerColor = MainColor,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                        textStyle =
                            TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            ),
                        singleLine = false,
                        maxLines = 20,
                        enabled = !isLoading
                    )
                }
            }

            // 하단 버튼
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 31.dp)
                        .padding(bottom = 21.dp)
            ) {
                // 취소 버튼
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(61.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = LightGrayColor,
                            contentColor = DarkGrayColor
                        ),
                    enabled = !isLoading
                ) {
                    Text(
                        text = "취소",
                        style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 추가하기 버튼
                Button(
                    onClick = {
                        if (url.isBlank()) {
                            Toast.makeText(context, "링크를 입력해주세요", Toast.LENGTH_SHORT).show()
                        } else {
                            onAddScrap(url.trim(), memo.trim(), linkPreview)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(61.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MainColorDeep,
                            contentColor = Color.White
                        ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "추가하기",
                            style =
                                TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopBar(onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(MainColor),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // 뒤로가기 버튼
        IconButton(onClick = onBackClick, modifier = Modifier
            .padding(start = 11.dp)
            .size(40.dp)) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                contentDescription = "뒤로가기",
                tint = Color.Black,
                modifier = Modifier.size(30.dp)
            )
        }

        // 제목
        Text(
            text = "스크랩 추가하기",
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
            color = Color.Black,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddScrapScreenContentPreview() {
    Scrap2025Theme {
        AddScrapScreenContent(
            isLoading = false,
            linkPreviewUiState = null,
            url = "",
            memo = "",
            onUrlChange = {},
            onMemoChange = {},
            onAddScrap = { _, _, _ -> },
            onBack = {}
        )
    }
}

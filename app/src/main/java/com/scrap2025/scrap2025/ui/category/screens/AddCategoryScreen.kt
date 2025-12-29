package com.scrap2025.scrap2025.ui.category.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.ui.common.buttons.TwoButtons
import com.scrap2025.scrap2025.ui.common.topbars.TopBarWithBack
import com.scrap2025.scrap2025.ui.theme.BackgroundColor
import com.scrap2025.scrap2025.ui.theme.GrayColor
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorLight
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.viewmodel.AddCategoryViewModel

/** AddCategoryScreen - 카테고리 추가 화면 ViewModel을 통해 카테고리를 추가하고, Result 상태를 처리 */
@Composable
fun AddCategoryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddCategoryViewModel = hiltViewModel()
) {
    val addCategoryState by viewModel.addCategoryState.collectAsState()
    val context = LocalContext.current

    val categoryTitleInput by viewModel.categoryTitle.collectAsState()

    // Result 상태 처리
    LaunchedEffect(addCategoryState) {
        when (val state = addCategoryState) {
            is Result.Success -> {
                Toast.makeText(context, "카테고리가 추가되었습니다", Toast.LENGTH_SHORT).show()
                onBack()
                viewModel.resetState()
            }
            is Result.Error -> {
                Toast.makeText(context, state.message ?: "카테고리 추가 실패", Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            is Result.Loading -> {
                // Loading 상태는 버튼 비활성화로 처리
            }
            null -> {
                // 초기 상태
            }
        }
    }

    AddCategoryScreenContent(
        modifier = modifier,
        onBack = onBack,
        categoryTitleInput = categoryTitleInput,
        onValueChange = { newName -> viewModel.updateCategoryTitle(newName) },
        addCategoryState = addCategoryState,
        onAddCategory = { viewModel.addCategory() }
    )
}

@Composable
fun AddCategoryScreenContent(
    modifier: Modifier,
    onBack: () -> Unit,
    categoryTitleInput: String,
    onValueChange: (String) -> Unit,
    addCategoryState: Result<Unit>?,
    onAddCategory: () -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundColor,
        topBar = { TopBarWithBack(title = "카테고리 추가하기", onBack = onBack) },
        bottomBar = {
            TwoButtons(
                confirmText = "추가하기",
                onCancel = onBack,
                onConfirm = onAddCategory,
                state = addCategoryState
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 입력 필드
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 21.dp, vertical = 20.dp)
                            .background(MainColorLight, shape = RoundedCornerShape(8.dp))
                            .padding(12.dp)
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(MainColor, shape = RoundedCornerShape(10.dp))
                    ) {
                        TextField(
                            value = categoryTitleInput,
                            onValueChange = { newName -> onValueChange(newName) },
                            placeholder = {
                                Text(
                                    text = "카테고리명을 입력하세요",
                                    color = GrayColor,
                                    style = TextStyle(fontSize = 15.sp)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            textStyle = TextStyle(fontSize = 15.sp),
                            colors =
                                TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    cursorColor = Color.Black
                                ),
                            singleLine = true,
                            readOnly = addCategoryState is Result.Loading
                        )
                    }
                }
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun AddCategoryScreenContentPreview() {
    Scrap2025Theme {
        AddCategoryScreenContent(
            modifier = Modifier,
            onBack = {},
            categoryTitleInput = "",
            onValueChange = {},
            addCategoryState = null,
            onAddCategory = {}
        )
    }
}

package com.scrap2025.scrap2025.ui.scrap.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scrap2025.scrap2025.ui.theme.BackgroundColor
import com.scrap2025.scrap2025.ui.theme.DarkGrayColor
import com.scrap2025.scrap2025.ui.theme.LightGrayColor
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.viewmodel.EditMemoViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.scrap2025.scrap2025.data.remote.dto.ScrapMemoDto
import com.scrap2025.scrap2025.model.Result

@Composable
fun EditMemoScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditMemoViewModel = hiltViewModel()
) {
    val editMemoState = viewModel.editMemoState.collectAsState()

    EditMemoScreenContent(
        modifier = modifier,
        state = editMemoState.value,
        onBack = onBack,
        initialMemo = viewModel.initialMemo,
        onEditMemo = { memo -> viewModel.editMemo(memo) }
    )
}

@Composable
fun EditMemoScreenContent(
    state: Result<ScrapMemoDto>?,
    onBack: () -> Unit,
    initialMemo: String,
    onEditMemo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var memoText by remember { mutableStateOf(initialMemo) }

    when (state) {
        is Result.Loading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        is Result.Success -> {
            Toast.makeText(context, "메모가 수정되었습니다", Toast.LENGTH_SHORT).show()
            onBack()
        }

        is Result.Error -> {
            Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
        }
        else -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .background(BackgroundColor)
                ) {
                    // 톱바
                    TopBar(onBackClick = onBack)

                    // 메모 수정
                    Box(
                        modifier = Modifier
                            .padding(all = 22.dp)
                            .background(MainColor, RoundedCornerShape(12.dp))
                            .border(
                                width = 2.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        BasicTextField(
                            modifier = Modifier
                                .padding(all = 25.dp)
                                .fillMaxSize(),
                            value = memoText,
                            onValueChange = { newValue -> memoText = newValue},
                        )
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
                        ) {
                            Text(
                                text = "취소",
                                style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // 추가하기 버튼
                        Button(
                            onClick = { onEditMemo(memoText) },
                            modifier = Modifier
                                .weight(1f)
                                .height(61.dp),
                            shape = RoundedCornerShape(15.dp),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = MainColorDeep,
                                    contentColor = Color.White
                                ),
                        ) {
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
}

@Preview
@Composable
fun EditMemoScreenContentPreview() {
    Scrap2025Theme {
        EditMemoScreenContent(
            onBack = {},
            initialMemo = "test test test",
            onEditMemo = { },
            state = null
        )
    }
}
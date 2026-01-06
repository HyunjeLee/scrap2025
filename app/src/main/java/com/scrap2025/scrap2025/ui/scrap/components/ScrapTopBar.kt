package com.scrap2025.scrap2025.ui.scrap.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scrap2025.scrap2025.R
import com.scrap2025.scrap2025.model.CategoryItem
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.ui.theme.WarningColor

/**
 * TopBarWithTitle - 카테고리 제목 표시 및 편집 기능 '
 *
 * 내부적으로 편집 모드 상태를 관리하며, 모드에 따라 TopBarDefault/TopBarEditMode를 전환
 */
@Composable
fun ScrapTopBar(
    categoryId: String,
    categoryTitle: String,
    onUpdateCategory: (String, String) -> Unit,
    onDeleteCategory: () -> Unit,
    modifier: Modifier = Modifier
) {
    // === 내부 상태: 편집 모드 관리 ===
    var isEditMode by remember { mutableStateOf(false) }

    if (isEditMode) {
        TopBarEditMode(
            categoryTitle = categoryTitle,
            onSave = { newTitle ->
                onUpdateCategory(categoryId, newTitle)
                isEditMode = false
            },
            onCancel = { isEditMode = false },
            modifier = modifier
        )
    } else {
        TopBarDefault(
            categoryId = categoryId,
            categoryTitle = categoryTitle,
            onEditClick = { isEditMode = true },
            onDeleteClick = { onDeleteCategory() },
            modifier = modifier
        )
    }
}

/** TopBarDefault - 일반 모드 (카테고리명 표시 + 편집/삭제 버튼) */
@Composable
private fun TopBarDefault(
    categoryId: String,
    categoryTitle: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(53.dp)
            .background(MainColor),
        contentAlignment = Alignment.CenterStart
    ) {
        // 카테고리 제목
        Text(
            text = categoryTitle,
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 21.dp, end = 85.dp)
        )

        if (categoryId != CategoryItem.DEFAULT_ID && categoryId != CategoryItem.FAVORITE_ID) {
            // 편집/삭제 버튼
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 22.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                // 편집 버튼
                IconButton(onClick = onEditClick, modifier = Modifier.size(28.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = "편집",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // 삭제 버튼
                IconButton(onClick = { onDeleteClick() }, modifier = Modifier.size(28.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_trash),
                        contentDescription = "삭제",
                        tint = WarningColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

/** TopBarEditMode - 편집 모드 (TextField + 저장 버튼) 커서를 텍스트 끝으로 자동 이동하며, 유효성 검사를 수행 */
@Composable
private fun TopBarEditMode(
    categoryTitle: String,
    onSave: (String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    // === 내부 상태: 편집 중인 텍스트와 커서 위치를 포함한 TextFieldValue ===
    var textFieldState by
    remember(categoryTitle) {
        mutableStateOf(
            TextFieldValue(
                text = categoryTitle,
                selection = TextRange(categoryTitle.length) // 1. 초기 커서 위치: 맨 끝
            )
        )
    }

    // === 유효성 검사: 1자 이상 21자 이하 ===
    val isCategoryTitleValid = textFieldState.text.length in 1..21

    // === 포커스 관리 ===
    val focusRequester = remember { FocusRequester() }

    // 편집 모드 진입 시 자동 포커스
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(53.dp)
            .background(MainColor),
        contentAlignment = Alignment.CenterStart
    ) {
        // 편집용 TextField
        BasicTextField(
            value = textFieldState,
            onValueChange = { newValue -> textFieldState = newValue },
            modifier =
                Modifier
                    .padding(start = 21.dp, end = 60.dp)
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
            textStyle =
                TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                ),
            singleLine = true
        )

        // 저장 버튼
        IconButton(
            onClick = {
                if (isCategoryTitleValid) {
                    onSave(textFieldState.text)
                }
            },
            enabled = isCategoryTitleValid,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 22.dp)
                .size(28.dp)
        ) {
            Icon(
                painter =
                    painterResource(
                        if (isCategoryTitleValid) R.drawable.ic_check_filled
                        else R.drawable.ic_check_err
                    ),
                contentDescription = "저장",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarDefaultPreview() {
    Scrap2025Theme {
        TopBarDefault(
            categoryId = "",
            categoryTitle = "분류되지 않음",
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarEditModePreview() {
    Scrap2025Theme {
        TopBarEditMode(
            categoryTitle = "toomanyletter-toomanyletter-toomanyletter-toomanyletter",
            onSave = {},
            onCancel = {}
        )
    }
}

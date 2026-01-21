package com.scrap2025.scrap2025.ui.scrap.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scrap2025.scrap2025.R
import com.scrap2025.scrap2025.ui.theme.GrayColor
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.ui.theme.WarningColor

/** SelectionTopBar - 선택 모드일 때 상단 바 선택된 개수 표시 및 "전체" 선택 버튼 */
@Composable
fun SelectionTopBar(
    categoryTitle: String,
    selectedCount: Int,
    totalCount: Int,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        Box(
            modifier =
            modifier
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
        }

        Box(
            modifier =
            modifier
                .fillMaxWidth()
                .height(53.dp)
                .background(MainColor),
            contentAlignment = Alignment.CenterStart
        ) {
            // 선택 개수 표시
            Row(
                modifier = Modifier.padding(start = 21.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 전체 선택 체크박스
                Row(
                    modifier =
                    Modifier.clickable {
                        if (selectedCount == totalCount) {
                            onDeselectAll()
                        } else {
                            onSelectAll()
                        }
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter =
                            if (selectedCount == totalCount) {
                                painterResource(R.drawable.ic_check_filled)
                            } else {
                                painterResource(R.drawable.ic_check_unfilled)
                            },
                            contentDescription = "전체 선택",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "전체",
                            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
                            color = GrayColor
                        )
                    }
                }

                Spacer(modifier = Modifier.width(13.dp))

                // 선택 개수 표시
                Text(
                    text = "${selectedCount}개 선택됨",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }
    }
}

/** SelectionBottomBar - 선택 모드일 때 하단 바텀 바의 삭제, 이동, 공유, 즐겨찾기 버튼 */
@Composable
private fun SelectionBottomBar(
    onDelete: () -> Unit,
    onMove: () -> Unit,
    onShare: () -> Unit,
    onFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier =
        modifier
            .fillMaxWidth()
            .dropShadow(
                shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
                shadow =
                Shadow(
                    radius = 15.dp,
                    spread = 0.dp,
                    color = Color(0xFFBEBEBE).copy(alpha = 0.4f),
                    offset = DpOffset(x = 0.dp, y = (-3).dp)
                )
            ).clip(shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
            .background(MainColor)
            .padding(vertical = 10.dp)
            .windowInsetsPadding(WindowInsets.navigationBars),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 삭제
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onDelete() }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_trash),
                contentDescription = "삭제",
                tint = WarningColor,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "삭제",
                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
                color = Color.Black
            )
        }

        // 공유
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onShare() }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_share),
                contentDescription = "공유",
                tint = Color.Black,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "공유",
                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
                color = Color.Black
            )
        }

        // 이동
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onMove() }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_folder_move),
                contentDescription = "이동",
                tint = Color.Black,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "이동",
                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
                color = Color.Black
            )
        }

        // 즐겨찾기
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onFavorite() }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_fav_false),
                contentDescription = "즐겨찾기",
                tint = Color.Black,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "즐겨찾기",
                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
                color = Color.Black
            )
        }
    }
}

/** ScrapSelectionBottomBar - 선택 모드 하단 바의 비즈니스 로직(Toast 등)을 캡슐화한 래퍼 */
@Composable
fun ScrapSelectionBottomBar(
    onDelete: () -> Unit,
    onMove: () -> Unit,
    onShare: () -> Unit,
    onFavorite: (onSuccess: () -> Unit, onFailure: () -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    SelectionBottomBar(
        onDelete = onDelete,
        onMove = onMove,
        onShare = onShare,
        onFavorite = {
            onFavorite(
                { Toast.makeText(context, "즐겨찾기에 추가되었습니다", Toast.LENGTH_SHORT).show() },
                { Toast.makeText(context, "즐겨찾기 실패", Toast.LENGTH_SHORT).show() }
            )
        },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun SelectionBottomBarPreview() {
    Scrap2025Theme { SelectionBottomBar(onDelete = {}, onMove = {}, onShare = {}, onFavorite = {}) }
}

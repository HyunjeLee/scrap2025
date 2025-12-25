package com.scrap2025.scrap2025.ui.scrap.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DriveFileMove
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.gigamole.composeshadowsplus.common.shadowsPlus
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.ui.theme.BackgroundColor
import com.scrap2025.scrap2025.ui.theme.LightGrayColor
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorLight
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.ui.theme.WarningColor
import com.scrap2025.scrap2025.utils.UrlNavigator
import com.scrap2025.scrap2025.utils.copyToClipboard
import com.scrap2025.scrap2025.viewmodel.ScrapDetailViewModel
import java.time.LocalDateTime

@Composable
fun ScrapDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScrapDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val scrapDetailState by viewModel.scrapDetailState.collectAsStateWithLifecycle()

    when (scrapDetailState) {
        Result.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is Result.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("스크랩을 불러오는데 실패했습니다.")
                // todo: 재시도 로직  // pull to refresh
            }
        }

        is Result.Success<ScrapItem> -> {
            val scrapItem = (scrapDetailState as Result.Success<ScrapItem>).data

            ScrapDetailContent(
                scrapItem = scrapItem,
                onBack = onBack,
                onClipboardCopy = { url -> context.copyToClipboard(url) },
                onImageClick = { url -> UrlNavigator.openUrl(context, url)},
                modifier = modifier
            )
        }

    }
}

@Composable
fun ScrapDetailContent(
    scrapItem: ScrapItem,
    onBack: () -> Unit,
    onClipboardCopy: (String) -> Unit,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit = {},
    onEditMemo: () -> Unit = {},
    onShare: () -> Unit = {},
    onMove: () -> Unit = {},
    onToggleFavorite: () -> Unit = {},
) {
    Scaffold(
        topBar = { DetailTopBar(title = scrapItem.title, onBackClick = onBack) },
        bottomBar = {
            DetailBottomBar(
                onDelete = onDelete,
                onEditMemo = onEditMemo,
                onShare = onShare,
                onMove = onMove,
                onToggleFavorite = onToggleFavorite
            )
        },
        containerColor = BackgroundColor
    ) { innerPadding ->
        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 이미지 영역
            Box(
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(300.dp)
                        .height(160.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(15.dp),
                            spotColor = Color.Black.copy(alpha = 0.1f)
                        )
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                scrapItem.imageUrl?.let { imageUrl ->
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "스크랩 이미지",
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onImageClick(scrapItem.url) },
                        contentScale = ContentScale.Crop
                    )
                } ?: run {
                    Box(modifier.fillMaxSize().background(LightGrayColor))
                }
            }

            // URL & 본문내용 영역
            DetailSection(
                containerColor = MainColorLight,
                content = {
                    // URL 및 클립보드 Box
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    Color.White,
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 12.dp
                                )
                    ) {
                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically,
                            horizontalArrangement =
                                Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = scrapItem.url,
                                style = TextStyle(fontSize = 14.sp),
                                color = Color.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector =
                                    Icons.Default.ContentCopy,
                                contentDescription = "복사",
                                tint = Color.Black,
                                modifier =
                                    Modifier
                                        .size(24.dp)
                                        .clickable { onClipboardCopy(scrapItem.url) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 본문내용 라벨
                    Text(
                        text = "본문내용",
                        style =
                            TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 본문내용 컨텐츠 Box
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    Color.White,
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(16.dp)
                    ) {
                        Text(
                            text = scrapItem.title,
                            style =
                                TextStyle(
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                ),
                            color = Color.Black,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            )

            // 메모 영역
            DetailSection(
                containerColor = MainColorLight,
                content = {
                    // 메모 라벨
                    Text(
                        text = "메모",
                        style =
                            TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 메모 컨텐츠 Box
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .heightIn(min = 150.dp, max = 400.dp)
                                .background(
                                    Color.White,
                                    RoundedCornerShape(10.dp)
                                )
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                    ) {
                        Text(
                            text = scrapItem.memo ?: "",
                            style =
                                TextStyle(
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                ),
                            color = Color.Black,
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun DetailSection(
    containerColor: Color,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(containerColor, RoundedCornerShape(8.dp))
                .padding(16.dp)
    ) { content() }
}

@Composable
fun DetailTopBar(title: String, onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(MainColor),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // 뒤로가기 버튼
        IconButton(
            onClick = onBackClick, modifier = Modifier
                .padding(start = 11.dp)
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                contentDescription = "뒤로가기",
                tint = Color.Black,
                modifier = Modifier.size(30.dp)
            )
        }

        // 제목
        Text(
            modifier = Modifier.padding(end = 20.dp),
            text = title,
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun DetailBottomBar(
    onDelete: () -> Unit,
    onEditMemo: () -> Unit,
    onShare: () -> Unit,
    onMove: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MainColor)
            .shadowsPlus(
                color = Color.Black.copy(alpha = 0.1f),
                offset = DpOffset(0.dp, (-3).dp),
                radius = 15.dp,
            )
            .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)),
    )
    {
        Row(
            modifier =
                Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Outlined.Delete,
                iconTint = WarningColor,
                label = "삭제",
                onClick = onDelete
            )
            BottomNavItem(icon = Icons.Outlined.Edit, label = "메모 수정", onClick = onEditMemo)
            BottomNavItem(icon = Icons.Outlined.Share, label = "공유", onClick = onShare)
            BottomNavItem(
                icon = Icons.AutoMirrored.Outlined.DriveFileMove,
                label = "이동",
                onClick = onMove
            )
            BottomNavItem(
                icon = Icons.Outlined.StarBorder,
                label = "즐겨찾기",
                onClick = onToggleFavorite
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = Color.Black
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier.size(25.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, style = TextStyle(fontSize = 12.sp), color = Color.Black)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FullDataPreview() {
    val scrapItem = ScrapItem(
        id = "preview1",
        title = "구글 딥마인드의 새로운 AI 에이전트 'Antigravity' 공개\n구글 딥마인드의 새로운 AI 에이전트 'Antigravity' 공개\n",
        url = "https://deepmind.google/technologies/antigravity",
        memo =
            "이거 진짜 대단한 것 같음. 나중에 코딩할 때 꼭 써봐야지. 특히 Jetpack Compose 코드 짜주는 속도가 장난 아님." +
                    "이거 진짜 대단한 것 같음. 나중에 코딩할 때 꼭 써봐야지. 특히 Jetpack Compose 코드 짜주는 속도가 장난 아님." +
                    "이거 진짜 대단한 것 같음. 나중에 코딩할 때 꼭 써봐야지. 특히 Jetpack Compose 코드 짜주는 속도가 장난 아님." +
                    "이거 진짜 대단한 것 같음. 나중에 코딩할 때 꼭 써봐야지. 특히 Jetpack Compose 코드 짜주는 속도가 장난 아님." +
                    "이거 진짜 대단한 것 같음. 나중에 코딩할 때 꼭 써봐야지. 특히 Jetpack Compose 코드 짜주는 속도가 장난 아님." +
                    "이거 진짜 대단한 것 같음. 나중에 코딩할 때 꼭 써봐야지. 특히 Jetpack Compose 코드 짜주는 속도가 장난 아님." +
                    "이거 진짜 대단한 것 같음. 나중에 코딩할 때 꼭 써봐야지. 특히 Jetpack Compose 코드 짜주는 속도가 장난 아님." +
                    "이거 진짜 대단한 것 같음. 나중에 코딩할 때 꼭 써봐야지. 특히 Jetpack Compose 코드 짜주는 속도가 장난 아님." +
                    "이거 진짜 대단한 것 같음. 나중에 코딩할 때 꼭 써봐야지. 특히 Jetpack Compose 코드 짜주는 속도가 장난 아님." +
                    "이거 진짜 대단한 것 같음. 나중에 코딩할 때 꼭 써봐야지. 특히 Jetpack Compose 코드 짜주는 속도가 장난 아님.",
        imageUrl = "https://picsum.photos/seed/picsum/800/400",
        createdDate = LocalDateTime.now()
    )

    Scrap2025Theme {
        ScrapDetailContent(
            scrapItem = scrapItem,
            onBack = {},
            onClipboardCopy = {},
            onImageClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoImageAndMemoPreview() {
    val scrapItem = ScrapItem(
        id = "preview2",
        title = "짧은 제목",
        url = "exmaple url",
        createdDate = LocalDateTime.now()
    )

    Scrap2025Theme {
        ScrapDetailContent(
            scrapItem,
            onBack = {},
            onClipboardCopy = {},
            onImageClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    name = "다크모드 테스트",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun DarkModePreview() {
    val scrapItem = ScrapItem(
        id = "preview3",
        title = "다크모드에서도 잘 보이는지 확인",
        url = "https://example.com/darkmode",
        memo = "배경색과 텍스트 대비 확인용",
        createdDate = LocalDateTime.now()
    )

    Scrap2025Theme {
        ScrapDetailContent(
            scrapItem = scrapItem,
            onBack = {},
            onClipboardCopy = {},
            onImageClick = {}
        )
    }
}

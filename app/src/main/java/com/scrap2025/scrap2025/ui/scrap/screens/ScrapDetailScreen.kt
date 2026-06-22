package com.scrap2025.scrap2025.ui.scrap.screens

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.gigamole.composeshadowsplus.common.ShadowsPlusType
import com.gigamole.composeshadowsplus.common.shadowsPlus
import com.scrap2025.scrap2025.R
import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.ui.common.components.LoadingScreen
import com.scrap2025.scrap2025.ui.common.dialogs.CommonDeleteDialog
import com.scrap2025.scrap2025.ui.theme.BackgroundColor
import com.scrap2025.scrap2025.ui.theme.FavoriteColor
import com.scrap2025.scrap2025.ui.theme.LightGrayColor
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.ui.theme.MainColorLight
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import com.scrap2025.scrap2025.ui.theme.WarningColor
import com.scrap2025.scrap2025.utils.copyToClipboard
import com.scrap2025.scrap2025.utils.openUrl
import com.scrap2025.scrap2025.viewmodel.BottomBarViewModel
import com.scrap2025.scrap2025.viewmodel.ScrapDetailUiState
import com.scrap2025.scrap2025.viewmodel.ScrapDetailViewModel
import java.time.LocalDateTime

@Composable
fun ScrapDetailScreen(
    onBack: () -> Unit,
    onEditMemo: (String) -> Unit,
    onMove: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScrapDetailViewModel = hiltViewModel(),
    bottomBarViewModel: BottomBarViewModel =
        hiltViewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)
) {
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose { bottomBarViewModel.setBottomBar(null) }
    }

    when (val state = uiState) {
        is ScrapDetailUiState.Loading -> LoadingScreen()

        is ScrapDetailUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.message ?: "스크랩을 불러오는데 실패했습니다.")
                // todo: 재시도 로직  // pull to refresh
            }
        }

        is ScrapDetailUiState.Success -> {
            val scrapItem = state.scrapItem
            val isDeleteDialogVisible by
                viewModel.isDeleteDialogVisible.collectAsStateWithLifecycle()

            LaunchedEffect(scrapItem.isFavorite) {
                bottomBarViewModel.setBottomBar {
                    DetailBottomBar(
                        isFavorite = scrapItem.isFavorite,
                        onDelete = { viewModel.showDeleteDialog() },
                        initialMemo = scrapItem.memo,
                        onEditMemo = onEditMemo,
                        onShare = { shareScrap(context, scrapItem) },
                        onMove = onMove,
                        onToggleFavorite = {
                            viewModel.toggleFavorite(
                                onSuccess = {},
                                onFailure = {
                                    Toast
                                        .makeText(context, "즐겨찾기 실패", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            )
                        }
                    )
                }
            }

            if (isDeleteDialogVisible) {
                CommonDeleteDialog(
                    title = "정말 스크랩을 삭제하시겠습니까?",
                    confirmText = "삭제하기",
                    onDismiss = { viewModel.hideDeleteDialog() },
                    onConfirm = {
                        viewModel.deleteScrap(
                            onSuccess = {
                                viewModel.hideDeleteDialog()
                                onBack()
                                Toast
                                    .makeText(context, "스크랩 삭제 성공", Toast.LENGTH_SHORT)
                                    .show()
                            },
                            onFailure = {
                                Toast
                                    .makeText(context, "스크랩 삭제 실패", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        )
                    }
                )
            }
            ScrapDetailContent(
                scrapItem = scrapItem,
                onBack = onBack,
                onClipboardCopy = { url -> context.copyToClipboard(url) },
                onImageClick = { url -> context.openUrl(url) },
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
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = { DetailTopBar(title = scrapItem.title, onBackClick = onBack) },
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
                    ).clip(RoundedCornerShape(15.dp))
                    .background(Color.White)
                    .clickable { onImageClick(scrapItem.url) },
                contentAlignment = Alignment.Center
            ) {
                scrapItem.imageUrl?.let { imageUrl ->
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "스크랩 이미지",
                        modifier =
                        Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                    ?: run { Box(modifier = Modifier.fillMaxSize().background(LightGrayColor)) }
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
                            .background(Color.White, RoundedCornerShape(10.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
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
                                painter = painterResource(R.drawable.ic_clipboard),
                                contentDescription = "클립보드 복사",
                                tint = Color.Black,
                                modifier =
                                Modifier
                                    .size(24.dp)
                                    .clickable {
                                        onClipboardCopy(scrapItem.url)
                                    }
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
                            .heightIn(min = 60.dp, max = 200.dp)
                            .background(Color.White, RoundedCornerShape(10.dp))
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = scrapItem.description,
                            style = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
                            color = Color.Black,
                            maxLines = 20,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
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
                            .background(Color.White, RoundedCornerShape(10.dp))
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(
                            text = scrapItem.memo,
                            style =
                            TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            ),
                            color = Color.Black,
                            modifier = Modifier.fillMaxWidth()
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
    modifier: Modifier = Modifier
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
        modifier =
        modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(MainColor),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // 뒤로가기 버튼
        IconButton(
            onClick = onBackClick,
            modifier =
            Modifier
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
            modifier = Modifier
                .weight(1f)
                .padding(end = 20.dp)
                .basicMarquee(),
            text = title,
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
            color = Color.Black,
            maxLines = 1
        )
    }
}

@Composable
fun DetailBottomBar(
    isFavorite: Boolean,
    onDelete: () -> Unit,
    initialMemo: String,
    onEditMemo: (String) -> Unit,
    onShare: () -> Unit,
    onMove: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier =
        modifier
            .fillMaxWidth()
            .background(MainColor)
            .shadowsPlus(
                type = ShadowsPlusType.SoftLayer,
                color = Color.Black.copy(alpha = 0.1f),
                offset = DpOffset(0.dp, (-3).dp),
                radius = 15.dp
            ).clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
    ) {
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
                icon = painterResource(R.drawable.ic_trash),
                iconTint = WarningColor,
                label = "삭제",
                onClick = onDelete
            )
            BottomNavItem(
                icon = painterResource(R.drawable.ic_edit),
                label = "메모 수정",
                onClick = { onEditMemo(initialMemo) }
            )
            BottomNavItem(
                icon = painterResource(R.drawable.ic_share),
                label = "공유",
                onClick = onShare
            )
            BottomNavItem(
                icon = painterResource(R.drawable.ic_folder_move),
                label = "이동",
                onClick = onMove
            )
            BottomNavItem(
                icon =
                painterResource(
                    if (isFavorite) {
                        R.drawable.ic_fav_true
                    } else {
                        R.drawable.ic_fav_false
                    }
                ),
                iconTint = if (isFavorite) FavoriteColor else Color.Black,
                label = "즐겨찾기",
                onClick = onToggleFavorite
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: Painter,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = Color.Black
) {
    Column(
        modifier =
        modifier
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = icon,
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier.size(25.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, style = TextStyle(fontSize = 12.sp), color = Color.Black)
    }
}

private fun shareScrap(context: Context, scrapItem: ScrapItem) {
    val dataIntent: Intent =
        Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "[스크랩]\n${scrapItem.title}\n${scrapItem.url}")
            putExtra(Intent.EXTRA_TITLE, scrapItem.title)
        }
    val shareIntent = Intent.createChooser(dataIntent, null)
    context.startActivity(shareIntent)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FullDataPreview() {
    val scrapItem =
        ScrapItem(
            id = 1L,
            title =
            "구글 딥마인드의 새로운 AI 에이전트 'Antigravity' 공개\n구글 딥마인드의 새로운 AI 에이전트 'Antigravity' 공개\n",
            description =
            "안녕하세요, 안성재입니다.\n\n오늘 두 번째 [흑백2⚒\uFE0F리뷰]는\n네 분의 흑수저 셰프님들과 함께했습니다. \n삐딱한 천재, 중식 마녀, 부채도사 그리고 쓰리스타 킬러가 함께 했는데요. \n\n아직 [흑백요리사 시즌2]에 참여하고 계신 셰프님들이시다 보니\n생생하게 대결 당시 비하인드 스토리와 셰프님들의 각오도 들을 수 있었는데요,\n셰프님들과 더 많은 이야기를 나눌 수 있어 즐거웠던 시간이었습니다.\n\n그럼, 오늘도 재미있게 시청해 주세요.\n다음 주에 또 뵙겠습니다.\n\n*\n*\n\uD83C\uDFE0우리 집이 기네스 맛집\n기네스 나이트로서지의 혁신적인 기술로 집에서도 느낄 수 있는 기네스 생맥주의 퀄리티!\n\n\uD83D\uDCCC기네스 나이트로서지 패키지 특가 찬스!\n✅우리동네 GS: https://abr.ge/4x65k0v\n✅카카오톡 선물하기: https://gift.kakao.com/product/11319266\n\n\uD83D\uDC49패키지 구성품: 디바이스 1개 + 전용 캔 4캔 + 파인트 전용 잔 \n\uD83D\uDC49 완벽한 기네스 생맥주 한잔을 위한 디",
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
            categoryId = 0L,
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
    val scrapItem =
        ScrapItem(
            id = 2L,
            title = "짧은 제목",
            description = "description",
            url = "exmaple url",
            categoryId = 0L,
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

@Preview(showBackground = true, name = "다크모드 테스트", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DarkModePreview() {
    val scrapItem =
        ScrapItem(
            id = 3L,
            title = "다크모드에서도 잘 보이는지 확인",
            description = "description",
            url = "https://example.com/darkmode",
            memo = "배경색과 텍스트 대비 확인용",
            categoryId = 0L,
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

@Preview
@Composable
fun LoadingPreview() {
    Scrap2025Theme {
        Box(
            modifier =
            Modifier
                .fillMaxSize()
                .background(MainColor),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator(color = MainColorDeep) }
    }
}

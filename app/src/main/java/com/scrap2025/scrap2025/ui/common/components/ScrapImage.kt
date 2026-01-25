package com.scrap2025.scrap2025.ui.common.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun ScrapImage(imageUrl: String?, contentDescription: String?, modifier: Modifier = Modifier) {
    if (imageUrl != null) {
        val isFavicon =
            imageUrl.contains("favicon", ignoreCase = true) ||
                imageUrl.endsWith(".ico", ignoreCase = true) ||
                imageUrl.endsWith(".svg", ignoreCase = true)

        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = modifier,
            // 파비콘이나 SVG 아이콘이면 중앙 배치(Inside), 일반 이미지는 꽉 채우기(Crop)
            contentScale = if (isFavicon) ContentScale.Inside else ContentScale.Crop
        )
    }
}

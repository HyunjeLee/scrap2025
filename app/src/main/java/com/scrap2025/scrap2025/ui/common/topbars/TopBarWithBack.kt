package com.scrap2025.scrap2025.ui.common.topbars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scrap2025.scrap2025.ui.theme.MainColor

@Composable
fun TopBarWithBack(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
            modifier = modifier.fillMaxWidth().height(68.dp).background(MainColor),
            contentAlignment = Alignment.CenterStart
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { onBack() }, modifier = Modifier.padding(start = 11.dp)) {
                Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "뒤로가기",
                )
            }

            Text(
                    text = title,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    color = Color.Black,
                    modifier = Modifier.weight(1f).padding(bottom = 4.dp)
            )
        }
    }
}

@Preview
@Composable
fun TopBarPreview() {
    TopBarWithBack("카테고리 추가하기",{})
}

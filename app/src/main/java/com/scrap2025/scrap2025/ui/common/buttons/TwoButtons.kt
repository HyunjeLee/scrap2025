package com.scrap2025.scrap2025.ui.common.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scrap2025.scrap2025.ui.theme.DarkGrayColor
import com.scrap2025.scrap2025.ui.theme.LightGrayColor
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep

@Composable
fun TwoButtons(
    confirmText: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Row(
        modifier =
        modifier
            .fillMaxWidth()
            .padding(all = 22.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 취소 버튼
        Button(
            onClick = { onCancel() },
            modifier =
            Modifier
                .weight(1f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LightGrayColor),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "취소",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                color = DarkGrayColor
            )
        }

        // 추가하기 버튼
        Button(
            onClick = { onConfirm() },
            enabled = !isLoading && isEnabled,
            modifier =
            Modifier
                .weight(1f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MainColorDeep),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MainColor, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = confirmText,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                    color = MainColor
                )
            }
        }
    }
}

package com.scrap2025.scrap2025.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

fun Context.copyToClipboard(text: String, label: String = "URL") {
    // 시스템 서비스에서  클립보드 매니저를 가져오기
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)

    clipboard.setPrimaryClip(clip)

    Toast.makeText(this, "클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show()
}

package com.scrap2025.scrap2025.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri

const val MAIL_ADDRESS = "cs@teamscrap.co.kr"
const val INSTAGRAM_USERNAME = "teamscrap2026"

/**
 * URL을 유효성 검사 후 외부 브라우저로 엽니다.
 * @param url 열고자 하는 URL
 */
fun Context.openUrl(url: String) {
    try {
        // URL 유효성 검증
        if (url.isBlank()) {
            Toast.makeText(this, "URL이 비어있습니다", Toast.LENGTH_SHORT).show()
            return
        }

        // URI 파싱 및 유효성 검증
        val uri = url.toUri()

        // scheme 검증 (http, https만 허용)
        if (uri.scheme.isNullOrBlank()) {
            Toast.makeText(this, "올바르지 않은 URL 형식입니다", Toast.LENGTH_SHORT).show()
            return
        }

        if (uri.scheme !in listOf("http", "https")) {
            Toast.makeText(this, "http 또는 https URL만 지원합니다", Toast.LENGTH_SHORT).show()
            return
        }

        // Intent 생성 및 실행
        val intent = Intent(Intent.ACTION_VIEW, uri)

        this.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(this, "URL을 여는 중 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
        Log.e("UrlNavigator", "Error opening URL", e)
    }
}

fun Context.sendEmail(address: String) {
    val subject = "[Scrap2025] 문의사항 : "
    val body = "문의 내용을 입력해 주세요."

    val uriString =
        "mailto:$address" +
            "?subject=${Uri.encode(subject)}" +
            "&body=${Uri.encode(body)}"

    val intent =
        Intent(Intent.ACTION_SENDTO).apply {
            data = uriString.toUri()
        }

    val chooser = Intent.createChooser(intent, "이메일 앱 선택")
    this.startActivity(chooser)
}

fun Context.sendInstagramDM(username: String) {
    val uri = "https://ig.me/m/$username".toUri()
    val intent =
        Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.instagram.android") // 인스타그램 앱이 설치되어 있다면 앱으로
        }

    try {
        this.startActivity(intent)
    } catch (e: Exception) {
        // 인스타그램 앱이 없는 경우 브라우저로 열리도록 처리
        this.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}

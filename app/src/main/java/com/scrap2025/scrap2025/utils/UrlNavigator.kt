package com.scrap2025.scrap2025.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri

object UrlNavigator {
    /**
     * URL을 유효성 검사 후 외부 브라우저로 엽니다.
     * @param context Context
     * @param url 열고자 하는 URL
     */
    fun openUrl(context: Context, url: String) {
        try {
            // URL 유효성 검증
            if (url.isBlank()) {
                Toast.makeText(context, "URL이 비어있습니다", Toast.LENGTH_SHORT).show()
                return
            }

            // URI 파싱 및 유효성 검증
            val uri = url.toUri()

            // scheme 검증 (http, https만 허용)
            if (uri.scheme.isNullOrBlank()) {
                Toast.makeText(context, "올바르지 않은 URL 형식입니다", Toast.LENGTH_SHORT).show()
                return
            }

            if (uri.scheme !in listOf("http", "https")) {
                Toast.makeText(context, "http 또는 https URL만 지원합니다", Toast.LENGTH_SHORT).show()
                return
            }

            // Intent 생성 및 실행
            val intent = Intent(Intent.ACTION_VIEW, uri)

            // 해당 Intent를 처리할 수 있는 앱이 있는지 확인
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "URL을 열 수 있는 앱이 없습니다", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "URL을 열 수 있는 앱이 없습니다", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "URL을 여는 중 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
        }
    }
}

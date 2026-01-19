
# 시스템 및 로그 복원 (에러 분석용)
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# WebView 자바스크립트 인터페이스 (가져오기 기능 보호)
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keepattributes JavascriptInterface

# 소셜 로그인 (카카오 & 네이버)
-keep class com.kakao.sdk.** { *; }
-keepnames class com.kakao.sdk.** { *; }
-keep class com.navercorp.nid.** { *; }
-dontwarn com.navercorp.nid.**

# 네트워크 (Retrofit & OkHttp)
-keepattributes Signature, InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-dontwarn okio.**

# 의존성 주입 (Hilt & Dagger)
-keep class androidx.hilt.** { *; }
-keep class dagger.hilt.** { *; }
-dontwarn dagger.hilt.internal.Preconditions

# JSON 파싱 (kotlinx.serialization)
-keepnames class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <methods>;
}

# 기타 라이브러리 경고 무시
-dontwarn org.bouncycastle.jsse.**
-dontwarn org.conscrypt.*
-dontwarn org.openjsse.**
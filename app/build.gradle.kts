import java.util.Base64
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktlint)
}

android {
    namespace = "com.scrap2025.scrap2025"
    compileSdk = 36

    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(localPropertiesFile.inputStream())
    } else {
        // local.properties 파일 자체가 없으면 에러 발생
        throw GradleException("local.properties file found in root project.")
    }

    // CI/CD 환경을 위한 키스토어 파일 자동 생성 로직
    val storeFilePath = properties.getProperty("STORE_FILE") ?: "release-key.jks"
    if (storeFilePath.isNotEmpty()) {
        val storeFile = rootProject.file(storeFilePath)

        // 무조건 Base64 키가 있으면 복구 시도 (파일이 있든 없든 덮어쓰기)
        if (properties.containsKey("RELEASE_KEYSTORE_BASE64")) {
            println("--- Keystore Generation Start ---")
            try {
                val encoded = properties.getProperty("RELEASE_KEYSTORE_BASE64")
                println("Base64 String Length: ${encoded.length}")

                // 불필요한 공백/따옴표 제거
                val cleanEncoded = encoded.replace("\n", "").replace(" ", "").replace("\"", "")

                val decoded = Base64.getDecoder().decode(cleanEncoded)
                println("Decoded Bytes Size: ${decoded.size}")
                if (storeFile.exists()) {
                    println("Deleting existing keystore file...")
                    storeFile.delete()
                }
                storeFile.parentFile?.mkdirs()
                storeFile.writeBytes(decoded)

                println("Keystore generated at: ${storeFile.absolutePath}")

                // 파일 무결성 검사 (첫 바이트 확인)
                if (decoded.isNotEmpty()) {
                    // 0x30 (48) 이어야 PKCS12/JKS 헤더일 가능성이 높음
                    println("First Byte (Hex): ${String.format("%02X", decoded[0])}")
                }
            } catch (e: Exception) {
                println("Failed to generate keystore: ${e.message}")
                e.printStackTrace()
            }
            println("--- Keystore Generation End ---")
        }
    }

    // 키값을 가져오되, 없거나 비어있으면 에러를 발생시키는 함수
    fun getSecret(key: String): String {
        val value = properties.getProperty(key)
        if (value.isNullOrEmpty()) {
            throw GradleException("Key '$key' is missing or empty in local.properties")
        }
        return value
    }

    defaultConfig {
        applicationId = "com.scrap2025.scrap2025"
        minSdk = 26
        targetSdk = 36
        versionCode = 11
        versionName = "1.0.0-alpha.$versionCode"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            type = "String",
            name = "NAVER_CLIENT_ID",
            value = "\"${getSecret("NAVER_CLIENT_ID")}\""
        )
        buildConfigField(
            type = "String",
            name = "NAVER_CLIENT_SECRET",
            value = "\"${getSecret("NAVER_CLIENT_SECRET")}\""
        )
        buildConfigField(
            type = "String",
            name = "NAVER_CLIENT_NAME",
            value = "\"${properties.getProperty("NAVER_CLIENT_NAME", "scrap2025")}\""
        )

        buildConfigField(
            type = "String",
            name = "KAKAO_NATIVE_APP_KEY",
            value = "\"${getSecret("KAKAO_NATIVE_APP_KEY")}\""
        )
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = getSecret("KAKAO_NATIVE_APP_KEY")
    }

    // 서명 설정 정의
    signingConfigs {
        create("release") {
            try {
                storeFile = rootProject.file(storeFilePath)
                storePassword = properties.getProperty("STORE_PASSWORD")
                keyAlias = properties.getProperty("KEY_ALIAS")
                keyPassword = properties.getProperty("KEY_PASSWORD")

                // [디버깅 로그 추가]
                println("Signing Config Check: ")
                println("  StoreFile: ${storeFile?.absolutePath} (Exists: ${storeFile?.exists()})")
                println(
                    "  StorePassword: ${if (storePassword.isNullOrEmpty()) "MISSING" else "PRESENT"}"
                )
                println("  KeyAlias: ${if (keyAlias.isNullOrEmpty()) "MISSING" else "PRESENT"}")
                println(
                    "  KeyPassword: ${if (keyPassword.isNullOrEmpty()) "MISSING" else "PRESENT"}"
                )
            } catch (e: Exception) {
                println("Release signing config not found in local.properties: $e")
            }
        }
    }

    buildTypes {
        release {
            // 위에서 정의한 'release' 서명 설정 사용
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    flavorDimensions += "environment"

    productFlavors {
        val developUrl = "\"https://dev.teamscrap.co.kr/\""
        val productionUrl = "\"https://teamscrap.co.kr/\""

        // 개발용 Flavor
        create("dev") {
            dimension = "environment"

            // 한 폰에 두 앱을 깔기 위해 패키지명 뒤에 .dev를 붙임
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"

            // 개발 서버 URL 주입
            buildConfigField(
                type = "String",
                name = "BASE_URL",
                value = developUrl
            )

            // 앱 이름을 '스크랩(DEV)'으로 변경 (resValue 사용)
            resValue("string", "app_name", "스크랩(DEV)")
        }
        // 운영용 Flavor
        create("prod") {
            dimension = "environment"

            // 운영 서버 URL 주입
            buildConfigField(
                type = "String",
                name = "BASE_URL",
                value = productionUrl
            )

            // 실제 앱 이름
            resValue("string", "app_name", "스크랩")
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.tink.android)
    implementation(libs.coil.compose)

    // Naver Login SDK
    implementation(libs.naver.oauth)
    // Kakao Login SDK
    implementation(libs.kakao.oauth)

    // Network
    implementation(libs.retrofit)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.converter.kotlinx.serialization)
    implementation(libs.logging.interceptor)

    // jsoup for HTML parsing
    implementation(libs.jsoup)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // reorderable
    implementation(libs.reorderable)

    // compose-shadow plus
    implementation(libs.compose.shadows.plus)

    // Paging 3
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    // for test
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

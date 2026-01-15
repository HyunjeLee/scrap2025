package com.scrap2025.scrap2025.data.local

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.core.content.edit
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TinkManager @Inject constructor(@ApplicationContext private val context: Context) {

    init {
        AeadConfig.register()
    }

    private val aead: Aead by lazy {
        try {
            getOrGenerateKey()
        } catch (e: Exception) {
            // 키 로드 실패 시(재설치 등), 기존 키셋을 삭제하고 새로 생성
            Log.e("TinkManager", "Failed to load key, resetting keyset", e)
            context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE).edit { clear() }
            getOrGenerateKey()
        }
    }

    fun encrypt(data: String): String {
        return try {
            val bytes = aead.encrypt(data.toByteArray(Charsets.UTF_8), null)
            Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e("TinkManager", "Encryption failed: ${e.message}")
            e.printStackTrace()
            ""
        }
    }

    fun decrypt(encryptedData: String): String {
        return try {
            val bytes = Base64.decode(encryptedData, Base64.DEFAULT)
            val decrypted = aead.decrypt(bytes, null)
            String(decrypted, Charsets.UTF_8)
        } catch (e: Exception) {
            Log.e("TinkManager", "Decryption failed: ${e.message}")
            e.printStackTrace()
            ""
        }
    }

    private fun getOrGenerateKey(): Aead {
        return AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, PREF_FILE_NAME)
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
            .getPrimitive(Aead::class.java)
    }

    companion object {
        private const val KEYSET_NAME = "master_keyset"
        private const val PREF_FILE_NAME = "master_key_preference"
        private const val MASTER_KEY_URI = "android-keystore://master_key"
    }
}

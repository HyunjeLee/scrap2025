package com.scrap2025.scrap2025.data.local

import android.content.Context
import android.util.Base64
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
        AndroidKeysetManager.Builder()
                .withSharedPref(context, KEYSET_NAME, PREF_FILE_NAME)
                .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
                .withMasterKeyUri(MASTER_KEY_URI)
                .build()
                .keysetHandle
                .getPrimitive(Aead::class.java)
    }

    fun encrypt(data: String): String {
        return try {
            val bytes = aead.encrypt(data.toByteArray(Charsets.UTF_8), null)
            Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: Exception) {
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
            e.printStackTrace()
            ""
        }
    }

    companion object {
        private const val KEYSET_NAME = "master_keyset"
        private const val PREF_FILE_NAME = "master_key_preference"
        private const val MASTER_KEY_URI = "android-keystore://master_key"
    }
}

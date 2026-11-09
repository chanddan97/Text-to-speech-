package com.example.data.vault

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig

class ApiVaultManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("cinevoice_vault_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_GEMINI_API_KEY = "gemini_api_key"
        private const val KEY_CUSTOM_KEY_SAVED = "custom_key_saved"
    }

    /**
     * Gets the effective API key: either user-entered key from vault,
     * or BuildConfig.GEMINI_API_KEY if configured.
     */
    fun getApiKey(): String {
        val userKey = prefs.getString(KEY_GEMINI_API_KEY, "")?.trim() ?: ""
        if (userKey.isNotEmpty()) {
            return userKey
        }
        return try {
            val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
            val buildKey = field.get(null) as? String ?: ""
            if (buildKey.isNotBlank() && buildKey != "MY_GEMINI_API_KEY") buildKey else ""
        } catch (_: Throwable) {
            ""
        }
    }

    fun hasCustomKey(): Boolean {
        return prefs.getBoolean(KEY_CUSTOM_KEY_SAVED, false) &&
                !prefs.getString(KEY_GEMINI_API_KEY, "").isNullOrBlank()
    }

    fun isKeyConfigured(): Boolean {
        return getApiKey().isNotBlank()
    }

    fun saveApiKey(key: String) {
        prefs.edit()
            .putString(KEY_GEMINI_API_KEY, key.trim())
            .putBoolean(KEY_CUSTOM_KEY_SAVED, true)
            .apply()
    }

    fun clearApiKey() {
        prefs.edit()
            .remove(KEY_GEMINI_API_KEY)
            .putBoolean(KEY_CUSTOM_KEY_SAVED, false)
            .apply()
    }

    fun getMaskedKey(): String {
        val key = getApiKey()
        if (key.length <= 8) return if (key.isEmpty()) "Not Configured" else "••••••••"
        return "${key.take(6)}••••••••${key.takeLast(4)}"
    }

    fun validateKeyFormat(key: String): Boolean {
        val trimmed = key.trim()
        return trimmed.startsWith("AIzaSy") && trimmed.length >= 30
    }
}

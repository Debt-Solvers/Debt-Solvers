package com.example.loginapp

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenManager(context: Context) {

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private var instance: TokenManager? = null

        fun getInstance(context: Context): TokenManager {
            if (instance == null) {
                instance = TokenManager(context.applicationContext)
            }
            return instance!!
        }
    }

    fun getToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }

    fun getUserId(): String? {
        return sharedPreferences.getString("user_id", null)
    }

    fun saveToken(token: String, userId: String) {
        sharedPreferences.edit()
            .putString("auth_token", token)
            .putString("user_id", userId)
            .apply()

    }

    fun clearToken() {
        sharedPreferences.edit().remove("auth_token").remove("user_id").apply()
    }
}

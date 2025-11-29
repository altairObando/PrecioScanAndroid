package com.nacatamalitosoft.precioscan.lib.helpers

import android.content.Context
import androidx.core.content.edit

object TokenManager {

    private const val PREFS_NAME = "prefs_tokens"
    private const val KEY_ACCESS = "access_token"
    private const val KEY_REFRESH = "refresh_token"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveTokens(context: Context, access: String, refresh: String) {
        prefs(context).edit {
            putString(KEY_ACCESS, access)
                .putString(KEY_REFRESH, refresh)
        }
    }

    fun getAccessToken(context: Context) = prefs(context).getString(KEY_ACCESS, null)
    fun getRefreshToken(context: Context) = prefs(context).getString(KEY_REFRESH, null)

    fun clearTokens(context: Context) {
        prefs(context).edit { clear() }
    }
}

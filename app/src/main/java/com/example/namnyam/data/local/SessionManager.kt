package com.example.namnyam.data.local

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("namnyam_session", Context.MODE_PRIVATE)

    fun saveSession(
        token: String,
        userId: Long,
        userName: String,
        userEmail: String,
        userRole: String
    ) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putLong(KEY_USER_ID, userId)
            .putString(KEY_USER_NAME, userName)
            .putString(KEY_USER_EMAIL, userEmail)
            .putString(KEY_USER_ROLE, userRole)
            .apply()
    }

    fun getToken(): String {
        return prefs.getString(KEY_TOKEN, "") ?: ""
    }

    fun getUserId(): Long {
        return prefs.getLong(KEY_USER_ID, -1L)
    }

    fun getUserName(): String {
        return prefs.getString(KEY_USER_NAME, "") ?: ""
    }

    fun getUserEmail(): String {
        return prefs.getString(KEY_USER_EMAIL, "") ?: ""
    }

    fun getUserRole(): String {
        return prefs.getString(KEY_USER_ROLE, "") ?: ""
    }

    fun isLoggedIn(): Boolean {
        return getToken().isNotBlank()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_ROLE = "user_role"
    }
}
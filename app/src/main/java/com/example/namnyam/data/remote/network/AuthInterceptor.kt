package com.example.namnyam.data.remote.network

import android.content.Context
import com.example.namnyam.data.storage.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {

    private val tokenManager = TokenManager(context.applicationContext)

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = tokenManager.getToken()
        val path = original.url.encodedPath

        val needAuth = path != "/auth/login" && path != "/auth/register"

        val request = if (needAuth && !token.isNullOrBlank()) {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }

        return chain.proceed(request)
    }
}
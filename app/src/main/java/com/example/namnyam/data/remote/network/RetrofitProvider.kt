package com.example.namnyam.data.remote.network

import android.content.Context
import com.example.namnyam.data.remote.api.NamNyamApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {

    @Volatile
    private var api: NamNyamApi? = null

    fun getApi(context: Context): NamNyamApi {
        return api ?: synchronized(this) {
            api ?: buildRetrofit(context.applicationContext)
                .create(NamNyamApi::class.java)
                .also { api = it }
        }
    }

    private fun buildRetrofit(context: Context): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
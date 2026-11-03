package com.example.bi5.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Default MockAPI base URL. Can be changed at runtime in app settings if needed.
    var baseUrl: String = "https://6ae953ce1a7b15aaa5101a1e.mockapi.io/"
        set(value) {
            field = if (value.endsWith("/")) value else "$value/"
            _apiService = createApiService(field)
        }

    private fun createApiService(url: String): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(url)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private var _apiService: ApiService = createApiService(baseUrl)

    val apiService: ApiService
        get() = _apiService
}

package com.prayerreminder.app.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object ApiClientProvider {

    private const val DEFAULT_BASE_URL = "https://api.aladhan.com/"

    @Volatile
    private var retrofit: Retrofit? = null

    fun getPrayerApiService(baseUrl: String = DEFAULT_BASE_URL): PrayerApiService {
        val instance = retrofit ?: synchronized(this) {
            retrofit ?: buildRetrofit(baseUrl).also { retrofit = it }
        }
        return instance.create(PrayerApiService::class.java)
    }

    private fun buildRetrofit(baseUrl: String): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
    }
}


package com.prayerreminder.app.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface PrayerApiService {

    @GET("v1/timings")
    suspend fun getPrayerTimesByCoords(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int,
        @Query("date") date: String? = null
    ): ApiPrayerTimesResponse
}


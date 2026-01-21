package com.prayerreminder.app.data.api

import com.squareup.moshi.Json

data class ApiPrayerTimesResponse(
    @Json(name = "code") val code: Int?,
    @Json(name = "status") val status: String?,
    @Json(name = "data") val data: ApiPrayerData?
)

data class ApiPrayerData(
    @Json(name = "timings") val timings: ApiTimings?,
    @Json(name = "date") val date: ApiDateInfo?,
    @Json(name = "meta") val meta: ApiMeta?
)

data class ApiTimings(
    @Json(name = "Fajr") val fajr: String?,
    @Json(name = "Sunrise") val sunrise: String?,
    @Json(name = "Dhuhr") val dhuhr: String?,
    @Json(name = "Asr") val asr: String?,
    @Json(name = "Maghrib") val maghrib: String?,
    @Json(name = "Isha") val isha: String?
)

data class ApiDateInfo(
    @Json(name = "timestamp") val timestamp: String?
)

data class ApiMeta(
    @Json(name = "latitude") val latitude: Double?,
    @Json(name = "longitude") val longitude: Double?,
    @Json(name = "method") val method: ApiCalculationMethod?
)

data class ApiCalculationMethod(
    @Json(name = "id") val id: Int?
)


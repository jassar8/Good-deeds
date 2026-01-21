package com.prayerreminder.app.domain

data class AppSettings(
    val calculationMethod: Int = 4, // default method id, can be changed in Settings
    val theme: String = "system",
    val lastKnownCity: String? = null,
    val lastKnownLat: Double? = null,
    val lastKnownLon: Double? = null
)


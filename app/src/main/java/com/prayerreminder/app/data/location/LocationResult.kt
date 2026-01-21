package com.prayerreminder.app.data.location

data class LocationResult(
    val latitude: Double?,
    val longitude: Double?,
    val error: LocationError? = null
)

enum class LocationError {
    PERMISSION_DENIED,
    TIMEOUT,
    UNKNOWN
}


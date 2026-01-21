package com.prayerreminder.app.data.location

import android.content.Context
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class GeocodingService(
    private val context: Context
) {

    @Suppress("DEPRECATION")
    suspend fun getCityName(latitude: Double, longitude: Double): String? = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val results = geocoder.getFromLocation(latitude, longitude, 1)
            val address = results?.firstOrNull()
            address?.locality ?: address?.subAdminArea ?: address?.adminArea
        } catch (e: Exception) {
            null
        }
    }
}


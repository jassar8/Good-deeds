package com.prayerreminder.app.data.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationProvider private constructor(
    private val fusedClient: FusedLocationProviderClient
) {

    companion object {
        fun create(context: Context): LocationProvider {
            val client = LocationServices.getFusedLocationProviderClient(context.applicationContext)
            return LocationProvider(client)
        }
    }

    /**
     * Caller is responsible for checking location permissions before calling this.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocationOrNull(): LocationResult =
        suspendCancellableCoroutine { continuation ->
            fusedClient.lastLocation
                .addOnSuccessListener { location ->
                    if (continuation.isActive) {
                        if (location != null) {
                            continuation.resume(
                                LocationResult(
                                    latitude = location.latitude,
                                    longitude = location.longitude,
                                    error = null
                                )
                            )
                        } else {
                            continuation.resume(
                                LocationResult(
                                    latitude = null,
                                    longitude = null,
                                    error = LocationError.UNKNOWN
                                )
                            )
                        }
                    }
                }
                .addOnFailureListener {
                    if (continuation.isActive) {
                        continuation.resume(
                            LocationResult(
                                latitude = null,
                                longitude = null,
                                error = LocationError.UNKNOWN
                            )
                        )
                    }
                }
        }
}


package com.prayerreminder.app.data.repository

import com.prayerreminder.app.domain.AppSettings
import com.prayerreminder.app.domain.DomainPrayerTimes
import com.prayerreminder.app.domain.NextPrayerInfo
import com.prayerreminder.app.domain.ReminderSettings
import kotlinx.coroutines.flow.Flow

interface PrayerRepository {

    suspend fun resolveCurrentLocation(hasPermission: Boolean): LocationInfo

    suspend fun refreshTodayPrayerTimes(force: Boolean = false): DomainPrayerTimes

    fun observeTodayPrayerTimes(): Flow<DomainPrayerTimes?>

    fun observeReminderSettings(): Flow<ReminderSettings>

    suspend fun updateReminderSettings(update: ReminderSettings)

    fun observeSettings(): Flow<AppSettings>

    suspend fun updateCalculationMethod(method: Int)

    fun observeNextPrayerInfo(): Flow<NextPrayerInfo?>
}

data class LocationInfo(
    val city: String,
    val latitude: Double,
    val longitude: Double
)


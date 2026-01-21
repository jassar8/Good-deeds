package com.prayerreminder.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_times")
data class PrayerTimesEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateEpoch: Long,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val city: String,
    val lat: Double,
    val lon: Double,
    val method: Int
)


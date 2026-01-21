package com.prayerreminder.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class SettingsEntity(
    @PrimaryKey val id: Long = 0,
    val calculationMethod: Int,
    val theme: String,
    val lastKnownCity: String?,
    val lastKnownLat: Double?,
    val lastKnownLon: Double?
)


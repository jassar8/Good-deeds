package com.prayerreminder.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder_settings")
data class ReminderSettingsEntity(
    @PrimaryKey val id: Long = 0,
    val enableFajr: Boolean,
    val enableDhuhr: Boolean,
    val enableAsr: Boolean,
    val enableMaghrib: Boolean,
    val enableIsha: Boolean,
    val offsetMinutesFajr: Int?,
    val offsetMinutesSunrise: Int?,
    val offsetMinutesDhuhr: Int?,
    val offsetMinutesAsr: Int?,
    val offsetMinutesMaghrib: Int?,
    val offsetMinutesIsha: Int?
)


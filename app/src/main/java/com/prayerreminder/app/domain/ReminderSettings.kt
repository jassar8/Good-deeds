package com.prayerreminder.app.domain

data class ReminderSettings(
    val enableFajr: Boolean = true,
    val enableSunrise: Boolean = false,
    val enableDhuhr: Boolean = true,
    val enableAsr: Boolean = true,
    val enableMaghrib: Boolean = true,
    val enableIsha: Boolean = true,
    val offsetMinutesFajr: Int? = null,
    val offsetMinutesSunrise: Int? = null,
    val offsetMinutesDhuhr: Int? = null,
    val offsetMinutesAsr: Int? = null,
    val offsetMinutesMaghrib: Int? = null,
    val offsetMinutesIsha: Int? = null
)


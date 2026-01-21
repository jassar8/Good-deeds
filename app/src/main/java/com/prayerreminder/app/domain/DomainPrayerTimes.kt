package com.prayerreminder.app.domain

import java.time.LocalDate
import java.time.LocalTime

data class DomainPrayerTimes(
    val date: LocalDate,
    val times: Map<PrayerName, LocalTime>,
    val city: String,
    val latitude: Double,
    val longitude: Double,
    val calculationMethod: Int
)


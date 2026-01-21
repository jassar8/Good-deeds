package com.prayerreminder.app.domain

import java.time.Duration

data class NextPrayerInfo(
    val currentPrayer: PrayerName?,
    val nextPrayer: PrayerName?,
    val remaining: Duration,
    val elapsedSincePrevious: Duration
)


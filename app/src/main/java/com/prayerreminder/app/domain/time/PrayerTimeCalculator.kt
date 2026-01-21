package com.prayerreminder.app.domain.time

import com.prayerreminder.app.domain.DomainPrayerTimes
import com.prayerreminder.app.domain.NextPrayerInfo
import com.prayerreminder.app.domain.PrayerName
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

object PrayerTimeCalculator {

    fun parseTimeOrNull(value: String?): LocalTime? =
        value?.trim()?.takeIf { it.isNotEmpty() }?.let {
            LocalTime.parse(it)
        }

    fun determineNextPrayerInfo(
        todayPrayerTimes: DomainPrayerTimes,
        now: LocalDateTime
    ): NextPrayerInfo {
        val orderedPrayers = listOf(
            PrayerName.FAJR,
            PrayerName.SUNRISE,
            PrayerName.DHUHR,
            PrayerName.ASR,
            PrayerName.MAGHRIB,
            PrayerName.ISHA
        )

        val timesForToday = orderedPrayers.mapNotNull { name ->
            val time = todayPrayerTimes.times[name] ?: return@mapNotNull null
            name to time
        }

        val todayDate = todayPrayerTimes.date
        val nowTime = now.toLocalTime()

        var currentPrayer: PrayerName? = null
        var nextPrayer: PrayerName? = null
        var previousPrayerTime: LocalTime? = null
        var nextPrayerTime: LocalTime? = null

        for ((index, pair) in timesForToday.withIndex()) {
            val (name, time) = pair
            if (nowTime < time) {
                nextPrayer = name
                nextPrayerTime = time
                currentPrayer = if (index > 0) timesForToday[index - 1].first else null
                previousPrayerTime = if (index > 0) timesForToday[index - 1].second else null
                break
            }
        }

        if (nextPrayer == null) {
            // After the last prayer of the day: next is the first prayer of next day
            val last = timesForToday.lastOrNull()
            previousPrayerTime = last?.second
            currentPrayer = last?.first
            nextPrayer = timesForToday.firstOrNull()?.first
            nextPrayerTime = timesForToday.firstOrNull()?.second
        }

        val previousDateTime = previousPrayerTime?.let {
            LocalDateTime.of(todayDate, it)
        } ?: now
        val nextDateTime = nextPrayerTime?.let {
            LocalDateTime.of(todayDate, it)
                .let { dt -> if (dt.isBefore(now)) dt.plusDays(1) else dt }
        } ?: now

        val remaining = Duration.between(now, nextDateTime).coerceAtLeast(Duration.ZERO)
        val elapsed = Duration.between(previousDateTime, now).coerceAtLeast(Duration.ZERO)

        return NextPrayerInfo(
            currentPrayer = currentPrayer,
            nextPrayer = nextPrayer,
            remaining = remaining,
            elapsedSincePrevious = elapsed
        )
    }

    /**
     * Utility for approximate coordinate comparison when validating cached data.
     */
    fun areLocationsApproximatelyEqual(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
        thresholdDegrees: Double = 0.1
    ): Boolean {
        val latDiff = kotlin.math.abs(lat1 - lat2)
        val lonDiff = kotlin.math.abs(lon1 - lon2)
        return latDiff <= thresholdDegrees && lonDiff <= thresholdDegrees
    }
}


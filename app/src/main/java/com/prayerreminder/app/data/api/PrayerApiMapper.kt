package com.prayerreminder.app.data.api

import com.prayerreminder.app.domain.DomainPrayerTimes
import com.prayerreminder.app.domain.PrayerName
import com.prayerreminder.app.domain.time.PrayerTimeCalculator
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

object PrayerApiMapper {

    fun mapToDomain(response: ApiPrayerTimesResponse): DomainPrayerTimes? {
        val data = response.data ?: return null
        val timings = data.timings ?: return null

        val timestampSeconds = data.date?.timestamp?.toLongOrNull() ?: return null
        val date = Instant.ofEpochSecond(timestampSeconds)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val lat = data.meta?.latitude ?: 0.0
        val lon = data.meta?.longitude ?: 0.0
        val methodId = data.meta?.method?.id ?: 0

        val times: MutableMap<PrayerName, LocalTime> = linkedMapOf()
        PrayerTimeCalculator.parseTimeOrNull(timings.fajr)?.let { times[PrayerName.FAJR] = it }
        PrayerTimeCalculator.parseTimeOrNull(timings.sunrise)?.let { times[PrayerName.SUNRISE] = it }
        PrayerTimeCalculator.parseTimeOrNull(timings.dhuhr)?.let { times[PrayerName.DHUHR] = it }
        PrayerTimeCalculator.parseTimeOrNull(timings.asr)?.let { times[PrayerName.ASR] = it }
        PrayerTimeCalculator.parseTimeOrNull(timings.maghrib)?.let { times[PrayerName.MAGHRIB] = it }
        PrayerTimeCalculator.parseTimeOrNull(timings.isha)?.let { times[PrayerName.ISHA] = it }

        return DomainPrayerTimes(
            date = date,
            times = times,
            city = "", // will be filled from geocoding/location layer
            latitude = lat,
            longitude = lon,
            calculationMethod = methodId
        )
    }
}


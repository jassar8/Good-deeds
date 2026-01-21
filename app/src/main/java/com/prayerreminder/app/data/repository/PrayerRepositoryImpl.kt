package com.prayerreminder.app.data.repository

import com.prayerreminder.app.data.api.PrayerApiMapper
import com.prayerreminder.app.data.api.PrayerApiService
import com.prayerreminder.app.data.db.PrayerAppDatabase
import com.prayerreminder.app.data.db.PrayerTimesEntity
import com.prayerreminder.app.data.db.ReminderSettingsEntity
import com.prayerreminder.app.data.db.SettingsEntity
import com.prayerreminder.app.data.location.GeocodingService
import com.prayerreminder.app.data.location.LocationDefaults
import com.prayerreminder.app.data.location.LocationProvider
import com.prayerreminder.app.domain.AppSettings
import com.prayerreminder.app.domain.DomainPrayerTimes
import com.prayerreminder.app.domain.NextPrayerInfo
import com.prayerreminder.app.domain.PrayerName
import com.prayerreminder.app.domain.ReminderSettings
import com.prayerreminder.app.domain.reminder.ReminderScheduler
import com.prayerreminder.app.domain.time.PrayerTimeCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class PrayerRepositoryImpl(
    private val api: PrayerApiService,
    private val db: PrayerAppDatabase,
    private val locationProvider: LocationProvider,
    private val geocodingService: GeocodingService,
    private val reminderScheduler: ReminderScheduler,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : PrayerRepository {

    private val prayerTimesDao = db.prayerTimesDao()
    private val reminderSettingsDao = db.reminderSettingsDao()
    private val settingsDao = db.settingsDao()

    override suspend fun resolveCurrentLocation(hasPermission: Boolean): LocationInfo {
        val settings = settingsDao.observeSettings().firstOrDefault()
        return if (hasPermission) {
            val result = locationProvider.getCurrentLocationOrNull()
            val lat = result.latitude ?: settings?.lastKnownLat ?: LocationDefaults.DEFAULT_LATITUDE
            val lon = result.longitude ?: settings?.lastKnownLon ?: LocationDefaults.DEFAULT_LONGITUDE
            val city = geocodingService.getCityName(lat, lon) ?: settings?.lastKnownCity ?: LocationDefaults.DEFAULT_CITY

            val updatedSettings = (settings ?: SettingsEntity(
                id = 0,
                calculationMethod = 4,
                theme = "system",
                lastKnownCity = null,
                lastKnownLat = null,
                lastKnownLon = null
            )).copy(
                lastKnownCity = city,
                lastKnownLat = lat,
                lastKnownLon = lon
            )
            settingsDao.upsert(updatedSettings)

            LocationInfo(city = city, latitude = lat, longitude = lon)
        } else {
            val lat = settings?.lastKnownLat ?: LocationDefaults.DEFAULT_LATITUDE
            val lon = settings?.lastKnownLon ?: LocationDefaults.DEFAULT_LONGITUDE
            val city = settings?.lastKnownCity ?: LocationDefaults.DEFAULT_CITY
            LocationInfo(city = city, latitude = lat, longitude = lon)
        }
    }

    override suspend fun refreshTodayPrayerTimes(force: Boolean): DomainPrayerTimes {
        val today = LocalDate.now()
        val dateEpoch = today.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()

        val settings = settingsDao.observeSettings().firstOrDefault()
        val method = settings?.calculationMethod ?: 4
        val lat = settings?.lastKnownLat ?: LocationDefaults.DEFAULT_LATITUDE
        val lon = settings?.lastKnownLon ?: LocationDefaults.DEFAULT_LONGITUDE

        val cached = prayerTimesDao.getPrayerTimesForDate(dateEpoch, method)

        if (cached != null && !force) {
            return cached.toDomain()
        }

        val response = api.getPrayerTimesByCoords(
            latitude = lat,
            longitude = lon,
            method = method,
            date = null
        )

        val domain = PrayerApiMapper.mapToDomain(response)
            ?: cached?.toDomain()
            ?: throw IllegalStateException("Unable to load prayer times")

        val entity = domain.toEntity(dateEpoch)
        prayerTimesDao.upsertPrayerTimes(entity)

        scope.launch {
            val currentSettings = settingsDao.observeSettings().firstOrDefault()
            val updated = (currentSettings ?: SettingsEntity(
                id = 0,
                calculationMethod = method,
                theme = "system",
                lastKnownCity = domain.city.ifEmpty { LocationDefaults.DEFAULT_CITY },
                lastKnownLat = lat,
                lastKnownLon = lon
            ))
            settingsDao.upsert(updated)
        }

        return domain
    }

    override fun observeTodayPrayerTimes(): Flow<DomainPrayerTimes?> {
        val today = LocalDate.now()
        val dateEpoch = today.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()

        val settingsFlow = settingsDao.observeSettings()
        return settingsFlow.flatMapLatest { settings ->
            val method = settings?.calculationMethod ?: 4
            prayerTimesDao.observePrayerTimesForDate(dateEpoch, method)
        }.map { it?.toDomain() }
    }

    override fun observeReminderSettings(): Flow<ReminderSettings> {
        return reminderSettingsDao.observeReminderSettings()
            .map { entity ->
                if (entity == null) {
                    ReminderSettings()
                } else {
                    ReminderSettings(
                        enableFajr = entity.enableFajr,
                        enableSunrise = entity.offsetMinutesSunrise != null,
                        enableDhuhr = entity.enableDhuhr,
                        enableAsr = entity.enableAsr,
                        enableMaghrib = entity.enableMaghrib,
                        enableIsha = entity.enableIsha,
                        offsetMinutesFajr = entity.offsetMinutesFajr,
                        offsetMinutesSunrise = entity.offsetMinutesSunrise,
                        offsetMinutesDhuhr = entity.offsetMinutesDhuhr,
                        offsetMinutesAsr = entity.offsetMinutesAsr,
                        offsetMinutesMaghrib = entity.offsetMinutesMaghrib,
                        offsetMinutesIsha = entity.offsetMinutesIsha
                    )
                }
            }.distinctUntilChanged()
    }

    override suspend fun updateReminderSettings(update: ReminderSettings) {
        val entity = ReminderSettingsEntity(
            id = 0,
            enableFajr = update.enableFajr,
            enableDhuhr = update.enableDhuhr,
            enableAsr = update.enableAsr,
            enableMaghrib = update.enableMaghrib,
            enableIsha = update.enableIsha,
            offsetMinutesFajr = update.offsetMinutesFajr,
            offsetMinutesSunrise = update.offsetMinutesSunrise,
            offsetMinutesDhuhr = update.offsetMinutesDhuhr,
            offsetMinutesAsr = update.offsetMinutesAsr,
            offsetMinutesMaghrib = update.offsetMinutesMaghrib,
            offsetMinutesIsha = update.offsetMinutesIsha
        )
        reminderSettingsDao.upsert(entity)

        observeTodayPrayerTimes().filterNotNull().firstOrDefault()?.let { times ->
            reminderScheduler.scheduleRemindersForToday(times, update)
        }
    }

    override fun observeSettings(): Flow<AppSettings> {
        return settingsDao.observeSettings()
            .map { entity ->
                if (entity == null) {
                    AppSettings()
                } else {
                    AppSettings(
                        calculationMethod = entity.calculationMethod,
                        theme = entity.theme,
                        lastKnownCity = entity.lastKnownCity,
                        lastKnownLat = entity.lastKnownLat,
                        lastKnownLon = entity.lastKnownLon
                    )
                }
            }.distinctUntilChanged()
    }

    override suspend fun updateCalculationMethod(method: Int) {
        val current = settingsDao.observeSettings().firstOrDefault()
        val updated = (current ?: SettingsEntity(
            id = 0,
            calculationMethod = method,
            theme = "system",
            lastKnownCity = LocationDefaults.DEFAULT_CITY,
            lastKnownLat = LocationDefaults.DEFAULT_LATITUDE,
            lastKnownLon = LocationDefaults.DEFAULT_LONGITUDE
        )).copy(calculationMethod = method)

        settingsDao.upsert(updated)
        refreshTodayPrayerTimes(force = true)
    }

    override fun observeNextPrayerInfo(): Flow<NextPrayerInfo?> {
        val ticker = flow {
            while (true) {
                emit(Unit)
                kotlinx.coroutines.delay(1000)
            }
        }

        return combine(
            observeTodayPrayerTimes(),
            ticker
        ) { times, _ ->
            val nonNullTimes = times ?: return@combine null
            val now = LocalDateTime.now()
            PrayerTimeCalculator.determineNextPrayerInfo(nonNullTimes, now)
        }
    }

    private fun PrayerTimesEntity.toDomain(): DomainPrayerTimes {
        val date = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(dateEpoch),
            ZoneId.systemDefault()
        ).toLocalDate()

        val times = linkedMapOf<PrayerName, java.time.LocalTime>()
        times[PrayerName.FAJR] = java.time.LocalTime.parse(fajr)
        times[PrayerName.SUNRISE] = java.time.LocalTime.parse(sunrise)
        times[PrayerName.DHUHR] = java.time.LocalTime.parse(dhuhr)
        times[PrayerName.ASR] = java.time.LocalTime.parse(asr)
        times[PrayerName.MAGHRIB] = java.time.LocalTime.parse(maghrib)
        times[PrayerName.ISHA] = java.time.LocalTime.parse(isha)

        return DomainPrayerTimes(
            date = date,
            times = times,
            city = city,
            latitude = lat,
            longitude = lon,
            calculationMethod = method
        )
    }

    private fun DomainPrayerTimes.toEntity(dateEpoch: Long): PrayerTimesEntity {
        return PrayerTimesEntity(
            dateEpoch = dateEpoch,
            fajr = times[PrayerName.FAJR]?.toString().orEmpty(),
            sunrise = times[PrayerName.SUNRISE]?.toString().orEmpty(),
            dhuhr = times[PrayerName.DHUHR]?.toString().orEmpty(),
            asr = times[PrayerName.ASR]?.toString().orEmpty(),
            maghrib = times[PrayerName.MAGHRIB]?.toString().orEmpty(),
            isha = times[PrayerName.ISHA]?.toString().orEmpty(),
            city = city,
            lat = latitude,
            lon = longitude,
            method = calculationMethod
        )
    }
}

private suspend fun <T> Flow<T>.firstOrDefault(): T? {
    var value: T? = null
    kotlinx.coroutines.withContext(Dispatchers.IO) {
        collect {
            value = it
            throw kotlinx.coroutines.CancellationException()
        }
    }
    return value
}


package com.prayerreminder.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.prayerreminder.app.data.api.ApiClientProvider
import com.prayerreminder.app.data.db.PrayerAppDatabase
import com.prayerreminder.app.data.location.GeocodingService
import com.prayerreminder.app.data.location.LocationProvider
import com.prayerreminder.app.data.repository.LocationInfo
import com.prayerreminder.app.data.repository.PrayerRepository
import com.prayerreminder.app.data.repository.PrayerRepositoryImpl
import com.prayerreminder.app.data.reminder.ReminderSchedulerStub
import com.prayerreminder.app.domain.HomeUiState
import com.prayerreminder.app.domain.PrayerName
import com.prayerreminder.app.domain.ReminderSettings
import com.prayerreminder.app.domain.time.PrayerTimeCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState

    private val repository: PrayerRepository

    private var lastLocationInfo: LocationInfo? = null

    init {
        val context = application.applicationContext
        val api = ApiClientProvider.getPrayerApiService()
        val db = PrayerAppDatabase.getInstance(context)
        val locationProvider = LocationProvider.create(context)
        val geocodingService = GeocodingService(context)
        val reminderScheduler = ReminderSchedulerStub()

        repository = PrayerRepositoryImpl(
            api = api,
            db = db,
            locationProvider = locationProvider,
            geocodingService = geocodingService,
            reminderScheduler = reminderScheduler,
            scope = viewModelScope
        )

        observeFlows()
    }

    private fun observeFlows() {
        viewModelScope.launch {
            repository.observeTodayPrayerTimes().collectLatest { times ->
                _uiState.value = _uiState.value.copy(
                    todayPrayerTimes = times,
                    isLoading = false
                )
            }
        }

        viewModelScope.launch {
            repository.observeReminderSettings().collectLatest { settings ->
                _uiState.value = _uiState.value.copy(
                    reminderSettings = settings
                )
            }
        }

        viewModelScope.launch {
            repository.observeNextPrayerInfo().collectLatest { info ->
                _uiState.value = _uiState.value.copy(
                    nextPrayerInfo = info
                )
            }
        }

        viewModelScope.launch {
            repository.observeSettings().collectLatest { settings ->
                _uiState.value = _uiState.value.copy(
                    city = settings.lastKnownCity
                )
            }
        }
    }

    fun onLocationPermissionResult(granted: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                lastLocationInfo = repository.resolveCurrentLocation(granted)
                repository.refreshTodayPrayerTimes(force = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Error loading prayer times"
                )
            }
        }
    }

    fun onPrayerReminderToggled(prayerName: PrayerName, enabled: Boolean) {
        viewModelScope.launch {
            val current = _uiState.value.reminderSettings
            val updated = when (prayerName) {
                PrayerName.FAJR -> current.copy(enableFajr = enabled)
                PrayerName.SUNRISE -> current.copy(enableSunrise = enabled)
                PrayerName.DHUHR -> current.copy(enableDhuhr = enabled)
                PrayerName.ASR -> current.copy(enableAsr = enabled)
                PrayerName.MAGHRIB -> current.copy(enableMaghrib = enabled)
                PrayerName.ISHA -> current.copy(enableIsha = enabled)
            }
            repository.updateReminderSettings(updated)
        }
    }
}


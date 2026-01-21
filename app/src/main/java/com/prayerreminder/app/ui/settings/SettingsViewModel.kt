package com.prayerreminder.app.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.prayerreminder.app.data.api.ApiClientProvider
import com.prayerreminder.app.data.db.PrayerAppDatabase
import com.prayerreminder.app.data.location.GeocodingService
import com.prayerreminder.app.data.location.LocationProvider
import com.prayerreminder.app.data.repository.PrayerRepository
import com.prayerreminder.app.data.repository.PrayerRepositoryImpl
import com.prayerreminder.app.data.reminder.ReminderSchedulerStub
import com.prayerreminder.app.domain.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings

    private val repository: PrayerRepository

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

        viewModelScope.launch {
            repository.observeSettings().collectLatest { settings ->
                _settings.value = settings
            }
        }
    }

    fun onThemeChanged(theme: String) {
        // Theme persistence could be wired here later.
        _settings.value = _settings.value.copy(theme = theme)
    }

    fun onCalculationMethodChanged(method: Int) {
        viewModelScope.launch {
            repository.updateCalculationMethod(method)
        }
    }
}


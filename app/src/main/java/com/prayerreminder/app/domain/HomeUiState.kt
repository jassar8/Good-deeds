package com.prayerreminder.app.domain

data class HomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val city: String? = null,
    val todayPrayerTimes: DomainPrayerTimes? = null,
    val nextPrayerInfo: NextPrayerInfo? = null,
    val reminderSettings: ReminderSettings = ReminderSettings()
)


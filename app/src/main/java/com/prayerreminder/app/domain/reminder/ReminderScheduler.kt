package com.prayerreminder.app.domain.reminder

import com.prayerreminder.app.domain.DomainPrayerTimes
import com.prayerreminder.app.domain.ReminderSettings

interface ReminderScheduler {
    suspend fun scheduleRemindersForToday(
        times: DomainPrayerTimes,
        settings: ReminderSettings
    )

    suspend fun cancelAllReminders()
}


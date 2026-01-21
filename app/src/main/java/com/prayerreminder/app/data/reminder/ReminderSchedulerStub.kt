package com.prayerreminder.app.data.reminder

import android.util.Log
import com.prayerreminder.app.domain.DomainPrayerTimes
import com.prayerreminder.app.domain.ReminderSettings
import com.prayerreminder.app.domain.reminder.ReminderScheduler

class ReminderSchedulerStub : ReminderScheduler {

    override suspend fun scheduleRemindersForToday(
        times: DomainPrayerTimes,
        settings: ReminderSettings
    ) {
        Log.d("ReminderSchedulerStub", "scheduleRemindersForToday: $times, $settings")
    }

    override suspend fun cancelAllReminders() {
        Log.d("ReminderSchedulerStub", "cancelAllReminders")
    }
}


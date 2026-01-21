package com.prayerreminder.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PrayerTimesEntity::class,
        ReminderSettingsEntity::class,
        SettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PrayerAppDatabase : RoomDatabase() {

    abstract fun prayerTimesDao(): PrayerTimesDao
    abstract fun reminderSettingsDao(): ReminderSettingsDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: PrayerAppDatabase? = null

        fun getInstance(context: Context): PrayerAppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PrayerAppDatabase::class.java,
                    "prayer_app_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}


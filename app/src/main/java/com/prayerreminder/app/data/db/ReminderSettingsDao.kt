package com.prayerreminder.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderSettingsDao {

    @Query("SELECT * FROM reminder_settings WHERE id = 0")
    fun observeReminderSettings(): Flow<ReminderSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: ReminderSettingsEntity)
}


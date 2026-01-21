package com.prayerreminder.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerTimesDao {

    @Query(
        """
        SELECT * FROM prayer_times
        WHERE dateEpoch = :dateEpoch
          AND method = :method
        LIMIT 1
        """
    )
    suspend fun getPrayerTimesForDate(
        dateEpoch: Long,
        method: Int
    ): PrayerTimesEntity?

    @Query(
        """
        SELECT * FROM prayer_times
        WHERE dateEpoch = :dateEpoch
          AND method = :method
        LIMIT 1
        """
    )
    fun observePrayerTimesForDate(
        dateEpoch: Long,
        method: Int
    ): Flow<PrayerTimesEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPrayerTimes(entity: PrayerTimesEntity)
}


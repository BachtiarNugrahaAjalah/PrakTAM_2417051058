package com.example.praktam_2417051058.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.ColumnInfo
import com.example.praktam_2417051058.data.local.entity.ActivityRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityRecord(record: ActivityRecordEntity)

    @Query("SELECT * FROM activity_records ORDER BY date DESC")
    fun getActivityRecords(): Flow<List<ActivityRecordEntity>>

    @Query("SELECT * FROM activity_records WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getActivityRecordsByDateRange(startDate: Long, endDate: Long): Flow<List<ActivityRecordEntity>>

    @Query("SELECT category_id, SUM(duration) as total_duration FROM activity_records WHERE date >= :startTime GROUP BY category_id")
    suspend fun getTotalDurationPerCategory(startTime: Long): List<CategoryDuration>

    @Update
    suspend fun updateActivityRecord(record: ActivityRecordEntity)

    @Delete
    suspend fun deleteActivityRecord(record: ActivityRecordEntity)
}

data class CategoryDuration(
    @ColumnInfo(name = "category_id") val categoryId: Int,
    @ColumnInfo(name = "total_duration") val totalDuration: Int
)

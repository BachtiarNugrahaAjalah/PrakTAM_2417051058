package com.example.praktam_2417051058.data.repository

import com.example.praktam_2417051058.data.local.dao.ActivityRecordDao
import com.example.praktam_2417051058.data.local.entity.ActivityRecordEntity
import com.example.praktam_2417051058.data.remote.model.ActivityCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import java.util.concurrent.TimeUnit

data class ActivitySummary(
    val category: ActivityCategory,
    val totalDuration: Int
)

class ActivityRepository(
    private val activityRecordDao: ActivityRecordDao,
    private val staticDataRepository: StaticDataRepository
) {
    
    // Task 1: Management
    suspend fun insertActivity(record: ActivityRecordEntity) {
        activityRecordDao.insertActivityRecord(record)
    }

    suspend fun updateActivity(record: ActivityRecordEntity) {
        activityRecordDao.updateActivityRecord(record)
    }

    suspend fun deleteActivity(record: ActivityRecordEntity) {
        activityRecordDao.deleteActivityRecord(record)
    }

    // Task 2: Monitoring (History & Summary)
    fun getActivityRecords(): Flow<List<ActivityRecordEntity>> {
        return activityRecordDao.getActivityRecords()
    }

    suspend fun getCategoriesResult(): Result<List<ActivityCategory>> {
        return staticDataRepository.getStaticData().map { it.categories }
    }

    suspend fun getCategories(): List<ActivityCategory> {
        return getCategoriesResult().getOrNull() ?: emptyList()
    }

    // Fungsi Analisis Ringkasan Aktivitas
    fun getActivitySummary(): Flow<List<ActivitySummary>> {
        return getActivityRecords().map { records ->
            val categories = getCategories()
            
            // Grouping by CategoryId and summing the duration
            val grouped = records.groupBy { it.categoryId }
            
            val summaryList = grouped.mapNotNull { (categoryId, categoryRecords) ->
                val category = categories.find { it.categoryId == categoryId }
                if (category != null) {
                    val totalDuration = categoryRecords.sumOf { it.duration }
                    ActivitySummary(category, totalDuration)
                } else {
                    null
                }
            }
            
            // Urutkan dari durasi terpanjang
            summaryList.sortedByDescending { it.totalDuration }
        }
    }

    // Hitung Streak
    fun getStreak(): Flow<Int> {
        return getActivityRecords().map { records ->
            if (records.isEmpty()) return@map 0

            // Ambil semua tanggal unik (tanpa jam/menit/detik) dalam milidetik
            val activeDates = records.map { record ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = record.date
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.timeInMillis
            }.distinct().sortedDescending()

            if (activeDates.isEmpty()) return@map 0

            val today = Calendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)
            val todayMs = today.timeInMillis

            val yesterdayMs = todayMs - TimeUnit.DAYS.toMillis(1)

            // Jika tidak ada aktivitas hari ini atau kemarin, streak putus (0)
            // Kecuali jika hari ini belum selesai, tapi biasanya streak dihitung termasuk hari ini
            if (activeDates.first() < yesterdayMs) {
                return@map 0
            }

            var streak = 0
            var currentCheckDate = activeDates.first()

            // Jika aktivitas terakhir adalah kemarin, mulai dari kemarin
            // Jika hari ini, mulai dari hari ini
            
            for (date in activeDates) {
                if (date == currentCheckDate) {
                    streak++
                    currentCheckDate -= TimeUnit.DAYS.toMillis(1)
                } else {
                    break
                }
            }
            streak
        }
    }
}

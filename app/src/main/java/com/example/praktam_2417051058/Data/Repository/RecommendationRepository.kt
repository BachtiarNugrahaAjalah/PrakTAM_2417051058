package com.example.praktam_2417051058.data.repository

import com.example.praktam_2417051058.data.local.dao.ActivityRecordDao
import com.example.praktam_2417051058.data.local.dao.RecommendationResultDao
import com.example.praktam_2417051058.data.local.entity.RecommendationResultEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class RecommendationRepository(
    private val staticDataRepository: StaticDataRepository,
    private val activityRecordDao: ActivityRecordDao,
    private val recommendationResultDao: RecommendationResultDao
) {

    suspend fun analyzeAndGenerateRecommendations() = withContext(Dispatchers.IO) {
        // 1. Dapatkan awal hari (00:00) untuk perhitungan
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis

        // 2. Ambil agregasi durasi dari DAO (Sangat Efisien, ditangani SQLite)
        val categoryDurations = activityRecordDao.getTotalDurationPerCategory(startOfDay)
        val durationMap = categoryDurations.associate { it.categoryId to it.totalDuration }

        // 3. Dapatkan Rules dari Static Data
        val staticDataResult = staticDataRepository.getStaticData()
        if (staticDataResult.isFailure) return@withContext // Berhenti jika rules gagal diload
        val rules = staticDataResult.getOrNull()?.rules ?: emptyList()

        // 4. Proses Rule Matching (Parser O(1))
        for (rule in rules) {
            val parts = rule.ruleCondition.split("_")
            if (parts.size == 3) {
                val targetCategoryId = parts[0].toIntOrNull() ?: continue
                val operator = parts[1]
                val targetValue = parts[2].toIntOrNull() ?: continue

                // Dapatkan durasi user (jika tidak ada aktivitas, nilainya 0)
                val userDuration = durationMap[targetCategoryId] ?: 0

                val isMatched = when (operator) {
                    "<" -> userDuration < targetValue
                    ">" -> userDuration > targetValue
                    "==" -> userDuration == targetValue
                    "<=" -> userDuration <= targetValue
                    ">=" -> userDuration >= targetValue
                    else -> false
                }

                if (isMatched) {
                    // 5. Cek apakah sudah ada rekomendasi untuk rule ini hari ini
                    val alreadyGenerated = recommendationResultDao.hasRecommendationToday(rule.ruleId, startOfDay)
                    if (!alreadyGenerated) {
                        // Insert rekomendasi baru
                        val resultEntity = RecommendationResultEntity(
                            ruleId = rule.ruleId,
                            generatedDate = System.currentTimeMillis(),
                            status = "new"
                        )
                        recommendationResultDao.insertRecommendationResult(resultEntity)
                    }
                }
            }
        }
    }
}

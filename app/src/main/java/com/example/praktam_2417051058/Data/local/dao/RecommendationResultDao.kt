package com.example.praktam_2417051058.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.praktam_2417051058.data.local.entity.RecommendationResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecommendationResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendationResult(result: RecommendationResultEntity)

    @Update
    suspend fun updateRecommendationResult(result: RecommendationResultEntity)

    @Query("SELECT * FROM recommendation_results ORDER BY generated_date DESC")
    fun getRecommendationResults(): Flow<List<RecommendationResultEntity>>

    @Query("SELECT COUNT(*) > 0 FROM recommendation_results WHERE rule_id = :ruleId AND generated_date >= :startOfDay")
    suspend fun hasRecommendationToday(ruleId: Int, startOfDay: Long): Boolean
}

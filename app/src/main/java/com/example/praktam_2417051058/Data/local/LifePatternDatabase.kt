package com.example.praktam_2417051058.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.praktam_2417051058.data.local.dao.ActivityRecordDao
import com.example.praktam_2417051058.data.local.dao.RecommendationResultDao
import com.example.praktam_2417051058.data.local.entity.ActivityRecordEntity
import com.example.praktam_2417051058.data.local.entity.RecommendationResultEntity

@Database(
    entities = [
        ActivityRecordEntity::class,
        RecommendationResultEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class LifePatternDatabase : RoomDatabase() {
    abstract fun activityRecordDao(): ActivityRecordDao
    abstract fun recommendationResultDao(): RecommendationResultDao
}

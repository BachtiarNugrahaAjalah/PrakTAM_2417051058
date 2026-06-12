package com.example.praktam_2417051058.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recommendation_results")
data class RecommendationResultEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "result_id")
    val resultId: Int = 0,
    

    @ColumnInfo(name = "rule_id")
    val ruleId: Int, // Mengacu pada Static Data Gist
    
    @ColumnInfo(name = "generated_date")
    val generatedDate: Long, // Disimpan sebagai timestamp epoch
    val status: String // e.g. "pending", "completed", "ignored"
)

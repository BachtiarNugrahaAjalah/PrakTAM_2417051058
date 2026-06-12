package com.example.praktam_2417051058.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_records")
data class ActivityRecordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "record_id")
    val recordId: Int = 0,
    

    @ColumnInfo(name = "category_id")
    val categoryId: Int, // Mengacu pada Static Data Gist
    
    val duration: Int, // Durasi dalam menit
    val date: Long, // Disimpan sebagai timestamp epoch
    val notes: String? = null
)

package com.example.praktam_2417051058.data.model_legacy

import com.google.gson.annotations.SerializedName

data class Kegiatan(
    @SerializedName("namaKegiatan")
    val namaKegiatan: String,
    @SerializedName("deskripsi")
    val deskripsi: String,
    @SerializedName("waktu")
    val waktu: String,
    @SerializedName("image_url")
    val image_url: String
)

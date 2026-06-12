package com.example.praktam_2417051058.data.api_legacy

import com.example.praktam_2417051058.data.model_legacy.Kegiatan
import retrofit2.http.GET

interface ApiService {
    @GET("jenis_kegiatan.json")
    suspend fun getKegiatan(): List<Kegiatan>
}

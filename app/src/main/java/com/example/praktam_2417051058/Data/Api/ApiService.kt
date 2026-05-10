package com.example.praktam_2417051058.Data.Api

import com.example.praktam_2417051058.Data.Model.Kegiatan
import retrofit2.http.GET

interface ApiService {
    @GET("jenis_kegiatan.json")
    suspend fun getKegiatan(): List<Kegiatan>
}

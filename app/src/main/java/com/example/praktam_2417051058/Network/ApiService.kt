package com.example.praktam_2417051058.Network
import Model.Kegiatan
import retrofit2.http.GET

interface ApiService{
    @GET("nama_kegiatan.json")
    suspend fun getKegiatan(): List<Kegiatan>
}

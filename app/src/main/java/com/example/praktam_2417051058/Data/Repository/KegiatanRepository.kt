package com.example.praktam_2417051058.Data.Repository
import com.example.praktam_2417051058.Data.Api.RetrofitClient
import com.example.praktam_2417051058.Data.Model.Kegiatan

class KegiatanRepository {
    suspend fun getKegiatan(): List<Kegiatan>{
        return try {
            RetrofitClient.instance.getKegiatan()
        } catch (e: Exception){
            emptyList()
        }
    }
}
package com.example.praktam_2417051058.data.repository
import com.example.praktam_2417051058.data.api_legacy.RetrofitClient
import com.example.praktam_2417051058.data.model_legacy.Kegiatan

class KegiatanRepository {
    suspend fun getKegiatan(): List<Kegiatan>{
        return try {
            RetrofitClient.instance.getKegiatan()
        } catch (e: Exception){
            emptyList()
        }
    }
}

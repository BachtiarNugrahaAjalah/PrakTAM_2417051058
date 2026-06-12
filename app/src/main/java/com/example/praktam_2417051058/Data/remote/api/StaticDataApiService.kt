package com.example.praktam_2417051058.data.remote.api

import com.example.praktam_2417051058.data.remote.model.StaticDataResponse
import retrofit2.http.GET

interface StaticDataApiService {
    // Endpoint Gist. Nantinya baseUrl di Retrofit di-set ke domain Gist (misal: https://gist.githubusercontent.com/)
    // Ganti "raw/YOUR_GIST_ID/static_data.json" sesuai dengan path asli dari Gist Anda
    @GET("BachtiarNugrahaAjalah/a053f64f41664ed555c8f678c091eb2c/raw/50eaf767948e13daccc0b0f3be950e2bb038bb92/kategori_kegiatan.json") 
    suspend fun getStaticData(): StaticDataResponse
}

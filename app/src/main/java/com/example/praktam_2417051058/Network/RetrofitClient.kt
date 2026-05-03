package com.example.praktam_2417051058.Network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL =
        "https://gist.githubusercontent.com/BachtiarNugrahaAjalah/2eda577b548f7a35cd6af266b8bac06d/raw/41a094f8e54e48c228013d31465966f519688c28"
    // Ganti dengan URL API praktikum
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
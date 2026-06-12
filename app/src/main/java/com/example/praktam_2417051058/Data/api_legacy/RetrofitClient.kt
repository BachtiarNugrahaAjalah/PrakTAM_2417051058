package com.example.praktam_2417051058.data.api_legacy

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL =
        "https://gist.githubusercontent.com/BachtiarNugrahaAjalah/2eda577b548f7a35cd6af266b8bac06d/raw/7b6c39cb9409851f84542f96bd5e0c7d520bfc48/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

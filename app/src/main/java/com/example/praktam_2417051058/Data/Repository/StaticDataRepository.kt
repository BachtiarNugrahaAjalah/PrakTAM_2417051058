package com.example.praktam_2417051058.data.repository

import android.content.Context
import com.example.praktam_2417051058.data.remote.api.StaticDataApiService
import com.example.praktam_2417051058.data.remote.model.ActivityCategory
import com.example.praktam_2417051058.data.remote.model.RemoteUser
import com.example.praktam_2417051058.data.remote.model.StaticDataResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class StaticDataRepository(
    private val apiService: StaticDataApiService,
    private val context: Context,
    private val gson: Gson = Gson()
) {
    private val categoriesFileName = "activity_categories.json"
    private val usersFileName = "remote_users.json"

    suspend fun getStaticData(): Result<StaticDataResponse> = withContext(Dispatchers.IO) {
        try {
            val networkData = apiService.getStaticData()
            
            saveCategoriesToCache(networkData.categories)
            networkData.users?.let { saveUsersToCache(it) }
            
            Result.success(networkData)
        } catch (e: Exception) {
            val cachedCategories = getCategoriesFromCache()
            val cachedUsers = getUsersFromCache()
            
            if (cachedCategories != null) {
                Result.success(StaticDataResponse(cachedCategories, cachedUsers ?: emptyList()))
            } else {
                Result.failure(Exception("Data offline tidak tersedia atau corrupt.", e))
            }
        }
    }

    private fun saveCategoriesToCache(categories: List<ActivityCategory>) {
        try {
            File(context.filesDir, categoriesFileName).writeText(gson.toJson(categories))
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun saveUsersToCache(users: List<RemoteUser>) {
        try {
            File(context.filesDir, usersFileName).writeText(gson.toJson(users))
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun getCategoriesFromCache(): List<ActivityCategory>? {
        return try {
            val file = File(context.filesDir, categoriesFileName)
            if (file.exists()) {
                val type = object : TypeToken<List<ActivityCategory>>() {}.type
                gson.fromJson(file.readText(), type)
            } else null
        } catch (e: Exception) { null }
    }

    private fun getUsersFromCache(): List<RemoteUser>? {
        return try {
            val file = File(context.filesDir, usersFileName)
            if (file.exists()) {
                val type = object : TypeToken<List<RemoteUser>>() {}.type
                gson.fromJson(file.readText(), type)
            } else null
        } catch (e: Exception) { null }
    }
}

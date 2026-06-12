package com.example.praktam_2417051058.data.remote.model

import com.google.gson.annotations.SerializedName

data class StaticDataResponse(
    @SerializedName("categories")
    val categories: List<ActivityCategory>,
    @SerializedName("users")
    val users: List<RemoteUser>? = emptyList(),
    @SerializedName("rules")
    val rules: List<RecommendationRule>? = emptyList()
)

data class ActivityCategory(
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("category_name")
    val categoryName: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("illustration")
    val illustration: String? = null
)

data class RemoteUser(
    @SerializedName("id_user")
    val userId: Int,
    @SerializedName("nama")
    val name: String,
    @SerializedName("tanggal_lahir")
    val birthDate: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("foto")
    val photoUrl: String
)

data class RecommendationRule(
    @SerializedName("rule_id")
    val ruleId: Int,
    @SerializedName("rule_condition")
    val ruleCondition: String, // e.g., "sleep_duration < 6"
    @SerializedName("recommendation_text")
    val recommendationText: String
)

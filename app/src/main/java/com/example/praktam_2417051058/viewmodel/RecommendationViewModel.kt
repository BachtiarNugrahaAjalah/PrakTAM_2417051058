package com.example.praktam_2417051058.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.praktam_2417051058.data.local.dao.RecommendationResultDao
import com.example.praktam_2417051058.data.repository.RecommendationRepository
import com.example.praktam_2417051058.data.repository.StaticDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecommendationUiModel(
    val resultId: Int,
    val ruleText: String,
    val status: String,
    val generatedDate: Long
)

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val recommendationRepository: RecommendationRepository,
    private val recommendationResultDao: RecommendationResultDao,
    private val staticDataRepository: StaticDataRepository
) : ViewModel() {

    private val _recommendations = MutableStateFlow<List<RecommendationUiModel>>(emptyList())
    val recommendations: StateFlow<List<RecommendationUiModel>> = _recommendations.asStateFlow()

    init {
        analyzeData()
        loadRecommendations()
    }

    private fun analyzeData() {
        viewModelScope.launch {
            recommendationRepository.analyzeAndGenerateRecommendations()
        }
    }

    private fun loadRecommendations() {
        viewModelScope.launch {
            val staticDataResult = staticDataRepository.getStaticData()
            val rules = staticDataResult.getOrNull()?.rules ?: emptyList()
            val ruleMap = rules.associateBy { it.ruleId }

            recommendationResultDao.getRecommendationResults().collectLatest { results ->
                val uiModels = results.map { entity ->
                    val ruleText = ruleMap[entity.ruleId]?.recommendationText ?: "Rekomendasi tidak diketahui"
                    RecommendationUiModel(
                        resultId = entity.resultId,
                        ruleText = ruleText,
                        status = entity.status,
                        generatedDate = entity.generatedDate
                    )
                }
                _recommendations.value = uiModels
            }
        }
    }
}

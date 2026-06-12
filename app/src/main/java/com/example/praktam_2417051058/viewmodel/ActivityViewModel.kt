package com.example.praktam_2417051058.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.praktam_2417051058.data.local.entity.ActivityRecordEntity
import com.example.praktam_2417051058.data.remote.model.ActivityCategory
import com.example.praktam_2417051058.data.repository.ActivityRepository
import com.example.praktam_2417051058.data.repository.ActivitySummary
import com.example.praktam_2417051058.data.repository.StaticDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val staticDataRepository: StaticDataRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<ActivityCategory>>(emptyList())
    val categories: StateFlow<List<ActivityCategory>> = _categories.asStateFlow()

    private val _activityRecords = MutableStateFlow<List<ActivityRecordEntity>>(emptyList())
    val activityRecords: StateFlow<List<ActivityRecordEntity>> = _activityRecords.asStateFlow()

    private val _activitySummary = MutableStateFlow<List<ActivitySummary>>(emptyList())
    val activitySummary: StateFlow<List<ActivitySummary>> = _activitySummary.asStateFlow()

    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak.asStateFlow()

    init {
        fetchCategories()
        loadActivityData()
    }

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    private fun loadActivityData() {
        viewModelScope.launch {
            activityRepository.getActivityRecords().collect { records ->
                _activityRecords.value = records
            }
        }
        viewModelScope.launch {
            activityRepository.getActivitySummary().collect { summary ->
                _activitySummary.value = summary
            }
        }
        viewModelScope.launch {
            activityRepository.getStreak().collect { streakValue ->
                _streak.value = streakValue
            }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            val result = activityRepository.getCategoriesResult()
            result.onSuccess { cats ->
                if (cats.isNotEmpty()) {
                    _categories.value = cats
                }
            }.onFailure {
                _uiEvent.emit(UiEvent.ShowMessage("Gagal memuat data dari API"))
            }
        }
    }

    fun saveActivity(categoryId: Int?, durationStr: String, notes: String) {
        viewModelScope.launch {
            if (durationStr.isBlank()) {
                _uiEvent.emit(UiEvent.ShowMessage("Error: Durasi tidak boleh kosong"))
                return@launch
            }

            val duration = durationStr.toIntOrNull()
            if (duration == null || duration <= 0) {
                _uiEvent.emit(UiEvent.ShowMessage("Error: Durasi harus berupa angka lebih dari 0"))
                return@launch
            }

            if (categoryId == null) {
                _uiEvent.emit(UiEvent.ShowMessage("Error: Silakan pilih kategori aktivitas"))
                return@launch
            }

            val newRecord = ActivityRecordEntity(
                categoryId = categoryId,
                duration = duration,
                date = System.currentTimeMillis(),
                notes = notes
            )
            
            try {
                activityRepository.insertActivity(newRecord)
                _uiEvent.emit(UiEvent.NavigateBack("Aktivitas berhasil disimpan!"))
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage("Gagal menyimpan ke database lokal."))
            }
        }
    }

    fun deleteActivity(record: ActivityRecordEntity) {
        viewModelScope.launch {
            try {
                activityRepository.deleteActivity(record)
                _uiEvent.emit(UiEvent.ShowMessage("Aktivitas dihapus."))
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage("Gagal menghapus aktivitas."))
            }
        }
    }

    fun updateActivity(record: ActivityRecordEntity) {
        viewModelScope.launch {
            try {
                activityRepository.updateActivity(record)
                _uiEvent.emit(UiEvent.NavigateBack("Aktivitas diperbarui!"))
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage("Gagal memperbarui aktivitas."))
            }
        }
    }

    sealed class UiEvent {
        data class ShowMessage(val message: String) : UiEvent()
        data class NavigateBack(val successMessage: String) : UiEvent()
    }
}

package com.example.praktam_2417051058.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.praktam_2417051058.data.repository.StaticDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val staticDataRepository: StaticDataRepository,
    private val userDao: com.example.praktam_2417051058.data.local.dao.UserDao
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun login() {
        viewModelScope.launch {
            if (_email.value.isBlank() || _password.value.isBlank()) {
                _uiEvent.emit(UiEvent.ShowError("Email dan Password harus diisi"))
                return@launch
            }

            _isLoading.value = true
            val result = staticDataRepository.getStaticData()
            _isLoading.value = false

            result.onSuccess { data ->
                val user = data.users?.find { it.email == _email.value && it.password == _password.value }
                if (user != null) {
                    // Simpan ke local DB sebagai session (optional, tapi baik untuk integrasi)
                    userDao.insertUser(com.example.praktam_2417051058.data.local.entity.UserEntity(
                        userId = user.userId,
                        name = user.name,
                        email = user.email,
                        passwordHash = user.password // Di Gist masih raw password sesuai request
                    ))
                    _uiEvent.emit(UiEvent.LoginSuccess)
                } else {
                    _uiEvent.emit(UiEvent.ShowError("Email atau Password salah"))
                }
            }.onFailure {
                _uiEvent.emit(UiEvent.ShowError("Gagal mengambil data user: ${it.message}"))
            }
        }
    }

    sealed class UiEvent {
        object LoginSuccess : UiEvent()
        data class ShowError(val message: String) : UiEvent()
    }
}

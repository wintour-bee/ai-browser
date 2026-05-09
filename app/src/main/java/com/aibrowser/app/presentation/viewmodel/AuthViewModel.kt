package com.aibrowser.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aibrowser.app.domain.model.User
import com.aibrowser.app.domain.repository.AuthRepository
import com.aibrowser.app.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    init {
        checkLoginStatus()
    }
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            
            loginUseCase(email, password)
                .onSuccess { user ->
                    _currentUser.value = user
                    _isLoggedIn.value = true
                    _uiState.value = AuthUiState.Success(user)
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Login failed")
                }
        }
    }
    
    fun register(email: String, password: String, username: String, inviteCode: String? = null) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            
            registerUseCase(email, password, username, inviteCode)
                .onSuccess { user ->
                    _currentUser.value = user
                    _isLoggedIn.value = true
                    _uiState.value = AuthUiState.Success(user)
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Registration failed")
                }
        }
    }
    
    fun googleAuth(idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            
            authRepository.googleAuth(idToken)
                .onSuccess { user ->
                    _currentUser.value = user
                    _isLoggedIn.value = true
                    _uiState.value = AuthUiState.Success(user)
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Google auth failed")
                }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _currentUser.value = null
            _isLoggedIn.value = false
            _uiState.value = AuthUiState.Idle
        }
    }
    
    fun refreshUser() {
        viewModelScope.launch {
            getCurrentUserUseCase()
                .onSuccess { user ->
                    _currentUser.value = user
                }
                .onFailure { /* Handle silently */ }
        }
    }
    
    private fun checkLoginStatus() {
        _isLoggedIn.value = getCurrentUserUseCase.isLoggedIn()
    }
    
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

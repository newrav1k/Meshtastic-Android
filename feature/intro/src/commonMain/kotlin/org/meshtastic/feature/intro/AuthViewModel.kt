package org.meshtastic.feature.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.meshtastic.core.model.Authority
import org.meshtastic.core.repository.AuthRepository
import org.meshtastic.core.repository.AuthSessionRepository
import kotlin.time.Clock
import kotlin.time.Instant

@KoinViewModel
class AuthViewModel(
    private val authRepository: AuthRepository,
    private val authSessionRepository: AuthSessionRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val session = authSessionRepository.getSession()

            if (session == null) {
                _state.update {
                    it.copy(sessionState = SessionState.Unauthorized)
                }
                return@launch
            }

            val isExpired = runCatching {
                Instant.parse(session.expiresAt) <= Clock.System.now()
            }.getOrElse {
                true
            }

            if (isExpired) {
                authSessionRepository.clear()
                _state.update {
                    it.copy(sessionState = SessionState.Unauthorized)
                }
                return@launch
            }

            _state.update {
                it.copy(sessionState = SessionState.Authorized(session.authority))
            }
        }
    }

    fun onUsernameChange(value: String) {
        _state.value = _state.value.copy(username = value)
    }

    fun onPasswordChange(value: String) {
        _state.value = _state.value.copy(password = value)
    }

    fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            runCatching {
                authRepository.login(
                    username = _state.value.username,
                    password = _state.value.password,
                )
            }.onSuccess { session ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        sessionState = SessionState.Authorized(session.authority),
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        sessionState = SessionState.Unauthorized,
                        errorMessage = error.message ?: "Ошибка авторизации",
                    )
                }
            }
        }
    }
}

data class AuthUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val sessionState: SessionState = SessionState.Checking,
)

sealed interface SessionState {
    data object Checking : SessionState
    data object Unauthorized : SessionState
    data class Authorized(val authority: Authority) : SessionState
}
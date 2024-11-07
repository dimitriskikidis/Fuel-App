package com.dimitriskikidis.admin.fuelapp.presentation.signup

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskikidis.admin.fuelapp.data.remote.UserManager
import com.dimitriskikidis.admin.fuelapp.data.remote.requests.AdminSignUpRequest
import com.dimitriskikidis.admin.fuelapp.domain.repository.FuelRepository
import com.dimitriskikidis.admin.fuelapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: FuelRepository,
    private val userManager: UserManager
) : ViewModel() {

    sealed class UiEvent {
        object NavigateToMainMenu : UiEvent()
        data class ShowMessage(val message: String) : UiEvent()
    }

    var email: String = ""
        private set
    var password: String = ""
        private set
    var confirmPassword: String = ""
        private set

    private val _state = MutableStateFlow(SignUpUiState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.OnEmailChange -> {
                email = event.email
            }
            is SignUpEvent.OnPasswordChange -> {
                password = event.password
            }
            is SignUpEvent.OnConfirmPasswordChange -> {
                confirmPassword = event.confirmPassword
            }
            is SignUpEvent.OnSignUp -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            emailError = null,
                            passwordError = null,
                            confirmPasswordError = null,
                            isLoading = true
                        )
                    }

                    var emailError: String? = null
                    if (email.isBlank()) {
                        emailError = "This field is required." // "The email cannot be empty."
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = "The email address is invalid."
                    }

                    var passwordError: String? = null
                    if (password.isBlank()) {
                        passwordError = "This field is required." // "The password cannot be empty."
                    } else if (password.length < 6) {
                        passwordError = "The password must be at least 6 characters long."
                    }

                    var confirmPasswordError: String? = null
                    if (password != confirmPassword && passwordError == null) {
                        confirmPasswordError = "The passwords do not match."
                    }

                    val errors = listOf(emailError, passwordError, confirmPasswordError)
                    val hasErrors = errors.any { it != null }

                    if (hasErrors) {
                        _state.update {
                            it.copy(
                                emailError = emailError,
                                passwordError = passwordError,
                                confirmPasswordError = confirmPasswordError,
                                isLoading = false
                            )
                        }
                        return@launch
                    }

                    val request = AdminSignUpRequest(
                        email = email,
                        password = password
                    )

                    when (val response = repository.signUp(request)) {
                        is Resource.Success -> {
                            val signUpResponse = response.data!!
                            val accessToken = signUpResponse.accessToken
                            userManager.setAccessToken(accessToken)
                            userManager.setEmail(email)
                            _uiEvent.send(UiEvent.NavigateToMainMenu)
                        }
                        is Resource.Error -> {
                            val message = response.message!!
                            _uiEvent.send(UiEvent.ShowMessage(message))
                            _state.update {
                                it.copy(isLoading = false)
                            }
                        }
                    }
                }
            }
        }
    }
}
package com.dimitriskikidis.fuelapp.presentation.signup

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskikidis.fuelapp.data.remote.UserManager
import com.dimitriskikidis.fuelapp.data.remote.requests.ConsumerSignUpRequest
import com.dimitriskikidis.fuelapp.domain.repository.FuelRepository
import com.dimitriskikidis.fuelapp.util.Resource
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
        object NavigateToMainNavGraph : UiEvent()
        data class ShowMessage(val message: String) : UiEvent()
    }

    private val _state = MutableStateFlow(SignUpUiState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var username: String = ""
        private set
    var email: String = ""
        private set
    var password: String = ""
        private set
    var confirmPassword: String = ""
        private set

    fun onUsernameChange(username: String) {
        this.username = username
    }

    fun onEmailChange(email: String) {
        this.email = email
    }

    fun onPasswordChange(password: String) {
        this.password = password
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        this.confirmPassword = confirmPassword
    }

    fun onSignUp() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    usernameError = null,
                    emailError = null,
                    passwordError = null,
                    confirmPasswordError = null,
                    isLoading = true
                )
            }

            username = username.trim()
            var usernameError: String? = null
            if (username.isBlank()) {
                usernameError = "This field is required."
            }

            email = email.trim()
            var emailError: String? = null
            if (email.isBlank()) {
                emailError = "This field is required."
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailError = "The email address is invalid."
            }

            var passwordError: String? = null
            if (password.isBlank()) {
                passwordError = "This field is required."
            } else if (password.length < 6) {
                passwordError = "The password must be at least 6 characters long."
            }

            var confirmPasswordError: String? = null
            if (password != confirmPassword && passwordError == null) {
                confirmPasswordError = "The passwords do not match."
            }

            val errors = listOf(usernameError, emailError, passwordError, confirmPasswordError)
            val hasErrors = errors.any { it != null }

            if (hasErrors) {
                _state.update {
                    it.copy(
                        usernameError = usernameError,
                        emailError = emailError,
                        passwordError = passwordError,
                        confirmPasswordError = confirmPasswordError,
                        isLoading = false
                    )
                }
                return@launch
            }

            val request = ConsumerSignUpRequest(
                username = username,
                email = email,
                password = password
            )

            when (val response = repository.signUp(request)) {
                is Resource.Success -> {
                    val signUpResponse = response.data!!
                    userManager.setUserData(
                        accessToken = signUpResponse.accessToken,
                        consumerId = signUpResponse.consumerId,
                        email = email,
                        username = username
                    )
                    _uiEvent.send(UiEvent.NavigateToMainNavGraph)
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
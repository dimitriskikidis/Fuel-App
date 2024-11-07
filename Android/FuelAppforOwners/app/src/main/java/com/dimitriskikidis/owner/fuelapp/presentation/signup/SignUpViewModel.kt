package com.dimitriskikidis.owner.fuelapp.presentation.signup

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskikidis.owner.fuelapp.data.remote.UserManager
import com.dimitriskikidis.owner.fuelapp.data.remote.requests.OwnerSignUpRequest
import com.dimitriskikidis.owner.fuelapp.domain.repository.FuelRepository
import com.dimitriskikidis.owner.fuelapp.util.Resource
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

    private val _state = MutableStateFlow(SignUpUiState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var firstName: String = ""
        private set
    var lastName: String = ""
        private set
    var email: String = ""
        private set
    var password: String = ""
        private set
    var confirmPassword: String = ""
        private set

    fun onFirstNameChange(firstName: String) {
        this.firstName = firstName
    }

    fun onLastNameChange(lastName: String) {
        this.lastName = lastName
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
                    isLoading = true,
                    firstNameError = null,
                    lastNameError = null,
                    emailError = null,
                    passwordError = null,
                    confirmPasswordError = null
                )
            }

            var firstNameError: String? = null
            if (firstName.isBlank()) {
                firstNameError = "This field is required."
            }

            var lastNameError: String? = null
            if (lastName.isBlank()) {
                lastNameError = "This field is required."
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

            val errors = listOf(firstNameError, lastNameError,
                emailError, passwordError, confirmPasswordError)
            val hasErrors = errors.any { it != null }

            if (hasErrors) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        firstNameError = firstNameError,
                        lastNameError = lastNameError,
                        emailError = emailError,
                        passwordError = passwordError,
                        confirmPasswordError = confirmPasswordError
                    )
                }
                return@launch
            }

            val request = OwnerSignUpRequest(
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName
            )

            when (val response = repository.signUp(request)) {
                is Resource.Success -> {
                    val signUpResponse = response.data!!
                    val accessToken = signUpResponse.accessToken
                    val ownerId = signUpResponse.ownerId
                    userManager.setData(
                        accessToken = accessToken,
                        ownerId = ownerId,
                        email = email,
                        firstName = firstName,
                        lastName = lastName
                    )
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
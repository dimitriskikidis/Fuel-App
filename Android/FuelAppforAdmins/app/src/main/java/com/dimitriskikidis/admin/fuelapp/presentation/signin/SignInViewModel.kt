package com.dimitriskikidis.admin.fuelapp.presentation.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskikidis.admin.fuelapp.data.remote.UserManager
import com.dimitriskikidis.admin.fuelapp.data.remote.requests.AdminSignInRequest
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
class SignInViewModel @Inject constructor(
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

    private val _state = MutableStateFlow(SignInUiState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            val isSignedIn: Boolean = userManager.getAccessToken() != null
            if (isSignedIn) {
                _uiEvent.send(UiEvent.NavigateToMainMenu)
            }
        }
    }

    fun onEvent(event: SignInEvent) {
        when (event) {
            is SignInEvent.OnEmailChange -> {
                email = event.email
            }
            is SignInEvent.OnPasswordChange -> {
                password = event.password
            }
            is SignInEvent.OnSignIn -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            error = null,
                            isLoading = true
                        )
                    }

                    val request = AdminSignInRequest(
                        email = email,
                        password = password
                    )

                    when (val response = repository.signIn(request)) {
                        is Resource.Success -> {
                            val signInResponse = response.data!!
                            val accessToken = signInResponse.accessToken
                            userManager.setAccessToken(accessToken)
                            userManager.setEmail(email)
                            _uiEvent.send(UiEvent.NavigateToMainMenu)
                        }
                        is Resource.Error -> {
                            if (response.isUnauthorized) {
                                val error = "Wrong credentials."
                                _state.update {
                                    it.copy(
                                        error = error,
                                        isLoading = false
                                    )
                                }
                                return@launch
                            }
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
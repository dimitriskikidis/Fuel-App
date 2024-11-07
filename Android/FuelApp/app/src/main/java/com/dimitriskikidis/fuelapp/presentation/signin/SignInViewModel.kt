package com.dimitriskikidis.fuelapp.presentation.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskikidis.fuelapp.data.remote.UserManager
import com.dimitriskikidis.fuelapp.data.remote.requests.ConsumerSignInRequest
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
class SignInViewModel @Inject constructor(
    private val repository: FuelRepository,
    private val userManager: UserManager
) : ViewModel() {

    sealed class UiEvent {
        object NavigateToMainNavGraph : UiEvent()
        data class ShowMessage(val message: String) : UiEvent()
    }

    private val _state = MutableStateFlow(SignInUiState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var email: String = ""
        private set
    var password: String = ""
        private set

    init {
        viewModelScope.launch {
            val isSignedIn: Boolean = userManager.getAccessToken() != null
            if (isSignedIn) {
                _uiEvent.send(UiEvent.NavigateToMainNavGraph)
            }
        }
    }

    fun onEmailChange(email: String) {
        this.email = email
    }

    fun onPasswordChange(password: String) {
        this.password = password
    }

    fun onSignIn() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    error = null,
                    isLoading = true
                )
            }

            val request = ConsumerSignInRequest(
                email = email,
                password = password
            )

            when (val response = repository.signIn(request)) {
                is Resource.Success -> {
                    val signInResponse = response.data!!
                    userManager.setUserData(
                        accessToken = signInResponse.accessToken,
                        consumerId = signInResponse.consumerId,
                        email = email,
                        username = signInResponse.username
                    )
                    _uiEvent.send(UiEvent.NavigateToMainNavGraph)
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
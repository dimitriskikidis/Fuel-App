package com.dimitriskikidis.fuelapp.presentation.userreviewlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskikidis.fuelapp.data.remote.UserManager
import com.dimitriskikidis.fuelapp.domain.models.UserReview
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
class UserReviewListViewModel @Inject constructor(
    private val repository: FuelRepository,
    private val userManager: UserManager
) : ViewModel() {

    sealed class UiEvent {
        data class ShowMessage(val message: String) : UiEvent()
        data class ShowMessageAndSignOut(val message: String) : UiEvent()
    }

    private val _state = MutableStateFlow(UserReviewListUiState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            getUserReviews()
        }
    }

    fun onUserReviewUpdate() {
        viewModelScope.launch {
            getUserReviews()
        }
    }

    fun onDeleteUserReview(userReview: UserReview) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val reviewId = userReview.id

            when (val response = repository.deleteReview(reviewId)) {
                is Resource.Success -> {
                    getUserReviews()
                }
                is Resource.Error -> {
                    if (response.isUnauthorized) {
                        handleUnauthorizedError()
                        return@launch
                    }
                    val message = "An error has occurred."
                    _uiEvent.send(UiEvent.ShowMessage(message))
                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }

    private suspend fun getUserReviews() {
        _state.update {
            it.copy(isLoading = true)
        }

        val consumerId = userManager.getConsumerId()

        when (val response = repository.getUserReviewsByConsumerId(consumerId)) {
            is Resource.Success -> {
                val userReviews = response.data!!
                _state.update {
                    it.copy(
                        isLoading = false,
                        userReviews = userReviews
                    )
                }
            }
            is Resource.Error -> {
                if (response.isUnauthorized) {
                    handleUnauthorizedError()
                    return
                }
                val message = "An error has occurred."
                _uiEvent.send(UiEvent.ShowMessage(message))
                _state.update {
                    it.copy(
                        isLoading = false,
                        userReviews = emptyList()
                    )
                }
            }
        }
    }

    private suspend fun handleUnauthorizedError() {
        userManager.clear()
        _uiEvent.send(
            UiEvent.ShowMessageAndSignOut(
                "Your session has expired. You must sign in to continue" +
                        " using the app."
            )
        )
    }
}
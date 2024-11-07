package com.dimitriskikidis.fuelapp.presentation.addeditreview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskikidis.fuelapp.data.remote.UserManager
import com.dimitriskikidis.fuelapp.data.remote.requests.ReviewCreateUpdateRequest
import com.dimitriskikidis.fuelapp.domain.models.FuelStation
import com.dimitriskikidis.fuelapp.domain.models.ReviewData
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
import kotlin.math.roundToInt

@HiltViewModel
class AddEditReviewViewModel @Inject constructor(
    private val repository: FuelRepository,
    private val userManager: UserManager
) : ViewModel() {

    sealed class UiEvent {
        object NavigateBackWithResult : UiEvent()
        data class ShowMessage(val message: String) : UiEvent()
        data class ShowMessageAndSignOut(val message: String) : UiEvent()
    }

    private val _state = MutableStateFlow(AddEditReviewUiState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var fuelStationId: Int? = null
        private set
    var currentReviewData: ReviewData? = null
        private set

    private val _canPublish = MutableStateFlow(false)
    val canPublish = _canPublish.asStateFlow()

    var rating: Float = 0f
        private set
    var text: String = ""
        private set

    fun onInitData(fuelStationId: Int, reviewData: ReviewData?) {
        this.fuelStationId = fuelStationId
        currentReviewData = reviewData
        reviewData?.let {
            rating = it.rating.toFloat()
            text = it.text
            _canPublish.update { true }
        }
    }

    fun onRatingChanged(rating: Float) {
        this.rating = rating
        _canPublish.update { rating != 0f }
    }

    fun onTextChanged(text: String) {
        this.text = text
    }

    fun onSubmit() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val request = ReviewCreateUpdateRequest(
                rating = rating.roundToInt(),
                text = text
            )

            if (currentReviewData == null) {
                val fuelStationId = this@AddEditReviewViewModel.fuelStationId!!
                val consumerId = userManager.getConsumerId()

                when (val response = repository.createReview(fuelStationId, consumerId, request)) {
                    is Resource.Success -> {
                        _uiEvent.send(UiEvent.NavigateBackWithResult)
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
            } else {
                val reviewId = currentReviewData!!.id

                when (val response = repository.updateReview(reviewId, request)) {
                    is Resource.Success -> {
                        _uiEvent.send(UiEvent.NavigateBackWithResult)
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
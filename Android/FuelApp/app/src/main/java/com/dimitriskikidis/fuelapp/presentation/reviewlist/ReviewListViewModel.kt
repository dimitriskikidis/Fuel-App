package com.dimitriskikidis.fuelapp.presentation.reviewlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskikidis.fuelapp.data.remote.UserManager
import com.dimitriskikidis.fuelapp.domain.models.FuelStation
import com.dimitriskikidis.fuelapp.domain.models.Review
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
class ReviewListViewModel @Inject constructor(
    private val repository: FuelRepository,
    private val userManager: UserManager
) : ViewModel() {

    sealed class UiEvent {
        object SetFragmentResult : UiEvent()
        data class ShowMessage(val message: String) : UiEvent()
        data class ShowMessageAndSignOut(val message: String) : UiEvent()
    }

    private val _state = MutableStateFlow(ReviewListUiState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var fuelStationId: Int? = null
        private set
    var userReview: Review? = null
        private set

    fun onInitFuelStationId(fuelStationId: Int) {
        this.fuelStationId = fuelStationId

        viewModelScope.launch {
            getReviews()
        }
    }

    fun onDeleteReview() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val reviewId = userReview!!.id

            when (val response = repository.deleteReview(reviewId)) {
                is Resource.Success -> {
                    _uiEvent.send(UiEvent.SetFragmentResult)
                    getReviews()
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

    fun onAddEditReviewComplete() {
        viewModelScope.launch {
            getReviews()
        }
    }

    private suspend fun getReviews() {
        _state.update {
            it.copy(isLoading = true)
        }

        val fuelStationId = this.fuelStationId!!

        when (val response = repository.getReviewsByFuelStationId(fuelStationId)) {
            is Resource.Success -> {
                var reviews = response.data!!

                val reviewCount = reviews.size
                val rating = if (reviewCount > 0) {
                    (reviews.sumOf { it.rating }.toDouble() / reviewCount.toDouble()).toFloat()
                } else {
                    0f
                }

                val counts = if (reviews.isNotEmpty()) {
                    (5 downTo 1).map { numStars ->
                        reviews.count { it.rating == numStars }
                    }
                } else {
                    emptyList()
                }

                val percentages = if (counts.isNotEmpty()) {
                    counts.map { count ->
                        ((count.toDouble() / reviews.size.toDouble()) * 100).roundToInt()
                    }
                } else {
                    emptyList()
                }

                val consumerId = userManager.getConsumerId()
                userReview = reviews.firstOrNull { it.consumerId == consumerId }

                userReview?.let { ur ->
                    reviews = reviews.filterNot { it.id == ur.id }
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        rating = rating,
                        reviewCount = reviewCount,
                        percentages = percentages,
                        counts = counts,
                        reviews = reviews
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
                        error = message,
                        rating = 0f,
                        reviewCount = 0,
                        percentages = emptyList(),
                        counts = emptyList(),
                        reviews = emptyList()
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
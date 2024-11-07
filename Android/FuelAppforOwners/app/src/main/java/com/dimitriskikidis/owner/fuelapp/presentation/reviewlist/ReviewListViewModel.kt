package com.dimitriskikidis.owner.fuelapp.presentation.reviewlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskikidis.owner.fuelapp.data.remote.UserManager
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
import kotlin.math.roundToInt

@HiltViewModel
class ReviewListViewModel @Inject constructor(
    private val repository: FuelRepository,
    private val userManager: UserManager
) : ViewModel() {

    sealed class UiEvent {
        data class ShowMessage(val message: String) : UiEvent()
        data class ShowMessageAndSignOut(val message: String) : UiEvent()
    }

    private val _state = MutableStateFlow(ReviewListUiState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            getReviews()
        }
    }

    private suspend fun getReviews() {
        _state.update {
            it.copy(isLoading = true)
        }

        val fuelStationId = userManager.getFuelStationId()!!.toInt()

        when (val response = repository.getReviewsByFuelStationId(fuelStationId)) {
            is Resource.Success -> {
                val reviews = response.data!!

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
                val message = response.message!!
                _uiEvent.send(UiEvent.ShowMessage(message))
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = message,
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
package com.dimitriskikidis.fuelapp.presentation.fuelstationdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskikidis.fuelapp.data.remote.UserManager
import com.dimitriskikidis.fuelapp.domain.models.Fuel
import com.dimitriskikidis.fuelapp.domain.models.FuelStation
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
class FuelStationDetailsViewModel @Inject constructor(
    private val repository: FuelRepository,
    private val userManager: UserManager
) : ViewModel() {

    sealed class UiEvent {
        data class ShowMessage(val message: String) : UiEvent()
        data class ShowMessageAndSignOut(val message: String) : UiEvent()
    }

    private val _state = MutableStateFlow(FuelStationDetailsUiState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _mapState = MutableStateFlow(MapUiState())
    val mapState = _mapState.asStateFlow()

    var fuelStationId: Int? = null
        private set

    fun onInitFuelStationId(fuelStationId: Int) {
        this.fuelStationId = fuelStationId

        viewModelScope.launch {
            getFuelStationAndFuels()
        }
    }

    fun onUserReviewUpdate() {
        viewModelScope.launch {
            getFuelStationAndFuels()
        }
    }

    private suspend fun getFuelStationAndFuels() {
        _state.update {
            it.copy(isLoading = true)
        }

        val fuelStationId = this.fuelStationId!!
        val fuelStation: FuelStation
        val fuels: List<Fuel>

        when (val response = repository.getFuelStationById(fuelStationId)) {
            is Resource.Success -> {
                fuelStation = response.data!!
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
                        hasError = true
                    )
                }
                return
            }
        }

        when (val response = repository.getFuelsByFuelStationId(fuelStationId)) {
            is Resource.Success -> {
                fuels = response.data!!

                _mapState.update {
                    it.copy(
                        latitude = fuelStation.latitude,
                        longitude = fuelStation.longitude
                    )
                }
                _state.update {
                    it.copy(
                        isLoading = false,
                        fuelStation = fuelStation,
                        fuels = fuels
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
                        hasError = true
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
package com.dimitriskikidis.owner.fuelapp.presentation.fuellist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskikidis.owner.fuelapp.data.remote.UserManager
import com.dimitriskikidis.owner.fuelapp.domain.models.Fuel
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
class FuelListViewModel @Inject constructor(
    private val repository: FuelRepository,
    private val userManager: UserManager
) : ViewModel() {

    sealed class UiEvent {
        data class ShowMessage(val message: String) : UiEvent()
        data class ShowMessageAndSignOut(val message: String) : UiEvent()
    }

    private val _state = MutableStateFlow(FuelListUiState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            getFuels()
        }
    }

    fun onAddEditFuelComplete() {
        viewModelScope.launch {
            getFuels()
        }
    }

    fun onDeleteFuelConfirm(fuel: Fuel) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val fuelId = fuel.id

            when (val response = repository.deleteFuel(fuelId)) {
                is Resource.Success -> {
                    getFuels()
                }
                is Resource.Error -> {
                    if (response.isUnauthorized) {
                        handleUnauthorizedError()
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

    private suspend fun getFuels() {
        _state.update {
            it.copy(isLoading = true)
        }

        val fuelStationId = userManager.getFuelStationId()!!.toInt()

        when (val response = repository.getFuelsByFuelStationId(fuelStationId)) {
            is Resource.Success -> {
                val fuels = response.data!!
                    .sortedBy { it.name }

                _state.update {
                    it.copy(
                        isLoading = false,
                        fuels = fuels
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
                        fuels = emptyList()
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
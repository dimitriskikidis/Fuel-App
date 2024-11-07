package com.dimitriskikidis.admin.fuelapp.presentation.fueltypelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskikidis.admin.fuelapp.data.remote.UserManager
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
class FuelTypeListViewModel @Inject constructor(
    private val repository: FuelRepository,
    private val userManager: UserManager
) : ViewModel() {

    sealed class UiEvent {
        data class ShowMessage(val title: String, val message: String) : UiEvent()
        data class ShowMessageAndSignOut(val message: String) : UiEvent()
    }

    private val _state = MutableStateFlow(FuelTypeListUiState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            getFuelTypes()
        }
    }

    fun onEvent(event: FuelTypeListEvent) {
        when (event) {
            is FuelTypeListEvent.OnFuelTypeAddEditComplete -> {
                viewModelScope.launch {
                    getFuelTypes()
                }
            }
            is FuelTypeListEvent.OnFuelTypeDeleteConfirm -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(isLoading = true)
                    }

                    val fuelType = event.fuelType

                    when (val response = repository.deleteFuelType(fuelType.id)) {
                        is Resource.Success -> {
                            _uiEvent.send(
                                UiEvent.ShowMessage(
                                    title = "Success",
                                    message = "The fuel type '${fuelType.name}' was deleted successfully."
                                )
                            )
                        }
                        is Resource.Error -> {
                            if (response.isUnauthorized) {
                                handleUnauthorizedError()
                                return@launch
                            }
                            val message = response.message!!
                            _uiEvent.send(
                                UiEvent.ShowMessage(
                                    title = "Error",
                                    message = message
                                )
                            )
                        }
                    }

                    getFuelTypes()
                }
            }
        }
    }

    private suspend fun getFuelTypes() {
        _state.update {
            it.copy(isLoading = true)
        }

        when (val response = repository.getFuelTypes()) {
            is Resource.Success -> {
                val fuelTypes = response.data!!

                _state.update {
                    it.copy(
                        isLoading = false,
                        fuelTypes = fuelTypes
                    )
                }
            }
            is Resource.Error -> {
                if (response.isUnauthorized) {
                    handleUnauthorizedError()
                    return
                }
                val message = response.message!!
                _uiEvent.send(
                    UiEvent.ShowMessage(
                        title = "Error",
                        message = message
                    )
                )
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = message,
                        fuelTypes = emptyList()
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
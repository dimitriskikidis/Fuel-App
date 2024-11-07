package com.dimitriskikidis.admin.fuelapp.presentation.addeditfueltype

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskikidis.admin.fuelapp.data.remote.UserManager
import com.dimitriskikidis.admin.fuelapp.data.remote.requests.FuelTypeCreateUpdateRequest
import com.dimitriskikidis.admin.fuelapp.domain.models.FuelType
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
class AddEditFuelTypeViewModel @Inject constructor(
    private val repository: FuelRepository,
    private val userManager: UserManager
) : ViewModel() {

    sealed class UiEvent {
        data class ShowMessageAndNavigateBack(
            val title: String,
            val message: String
        ) : UiEvent()

        data class ShowMessageAndSignOut(val message: String) : UiEvent()
    }

    private val _state = MutableStateFlow(AddEditFuelTypeUiState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var currentFuelType: FuelType? = null
        private set

    var fuelTypeName: String? = null
        private set

    fun onEvent(event: AddEditFuelTypeEvent) {
        when (event) {
            is AddEditFuelTypeEvent.OnInitFuelType -> {
                currentFuelType = event.currentFuelType
                fuelTypeName = event.currentFuelType.name
            }
            is AddEditFuelTypeEvent.OnNameChange -> {
                fuelTypeName = event.name
            }
            is AddEditFuelTypeEvent.OnAddSave -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            nameError = null,
                            isLoading = true
                        )
                    }

                    val nameExists = event.fuelTypeNames.any {
                        it == fuelTypeName && it != currentFuelType?.name
                    }

                    var nameError: String? = null
                    if (fuelTypeName.isNullOrBlank()) {
                        nameError = "This field is required." // "The name cannot be empty."
                    }
                    if (nameExists) {
                        nameError = "The fuel type '$fuelTypeName' already exists."
                    }

                    if (nameError != null) {
                        _state.update {
                            it.copy(
                                nameError = nameError,
                                isLoading = false
                            )
                        }
                        return@launch
                    }

                    val request = FuelTypeCreateUpdateRequest(
                        name = fuelTypeName!!
                    )

                    if (currentFuelType == null) {
                        when (val response = repository.createFuelType(request)) {
                            is Resource.Success -> {
                                _uiEvent.send(
                                    UiEvent.ShowMessageAndNavigateBack(
                                        title = "Success",
                                        message = "The fuel type '$fuelTypeName' was added successfully."
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
                                    UiEvent.ShowMessageAndNavigateBack(
                                        title = "Error",
                                        message = message
                                    )
                                )
                            }
                        }
                    } else {
                        val fuelTypeId = currentFuelType!!.id

                        when (val response = repository.updateFuelType(fuelTypeId, request)) {
                            is Resource.Success -> {
                                _uiEvent.send(
                                    UiEvent.ShowMessageAndNavigateBack(
                                        title = "Success",
                                        message = "The fuel type '${currentFuelType!!.name}' was updated successfully."
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
                                    UiEvent.ShowMessageAndNavigateBack(
                                        title = "Error",
                                        message = message
                                    )
                                )
                            }
                        }
                    }

                    _state.update {
                        it.copy(isLoading = false)
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
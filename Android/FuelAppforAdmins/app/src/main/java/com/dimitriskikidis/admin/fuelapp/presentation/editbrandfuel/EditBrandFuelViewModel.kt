package com.dimitriskikidis.admin.fuelapp.presentation.editbrandfuel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskikidis.admin.fuelapp.data.remote.UserManager
import com.dimitriskikidis.admin.fuelapp.data.remote.requests.BrandFuelUpdateRequest
import com.dimitriskikidis.admin.fuelapp.domain.models.BrandFuel
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
class EditBrandFuelViewModel @Inject constructor(
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

    private val _state = MutableStateFlow(EditBrandFuelUiState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var currentBrandFuel: BrandFuel? = null
        private set

    var brandFuelName: String? = null
        private set
    var brandFuelIsEnabled: Boolean? = null
        private set

    fun onInitBrandFuel(brandFuel: BrandFuel) {
        currentBrandFuel = brandFuel
        brandFuelName = brandFuel.name
        brandFuelIsEnabled = brandFuel.isEnabled
    }

    fun onNameChange(name: String) {
        brandFuelName = name
    }

    fun onCheckedChange(isChecked: Boolean) {
        brandFuelIsEnabled = isChecked
    }

    fun onSave() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    nameError = null,
                    isLoading = true
                )
            }

            if (brandFuelName.isNullOrBlank()) {
                val nameError = "This field is required." // "The name cannot be empty."
                _state.update {
                    it.copy(
                        nameError = nameError,
                        isLoading = false
                    )
                }
                return@launch
            }

            val request = BrandFuelUpdateRequest(
                name = brandFuelName!!,
                isEnabled = brandFuelIsEnabled!!
            )

            val brandFuelId = currentBrandFuel!!.id

            when (val response = repository.updateBrandFuel(brandFuelId, request)) {
                is Resource.Success -> {
                    _uiEvent.send(
                        UiEvent.ShowMessageAndNavigateBack(
                            title = "Success",
                            message = "The brand fuel '${currentBrandFuel!!.name}' was updated successfully."
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

            _state.update {
                it.copy(isLoading = false)
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
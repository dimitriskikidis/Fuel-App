package com.dimitriskikidis.fuelapp.presentation.loading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskikidis.fuelapp.data.preferences.PreferencesManager
import com.dimitriskikidis.fuelapp.data.remote.UserManager
import com.dimitriskikidis.fuelapp.domain.models.Brand
import com.dimitriskikidis.fuelapp.domain.models.FuelType
import com.dimitriskikidis.fuelapp.domain.repository.FuelRepository
import com.dimitriskikidis.fuelapp.presentation.DataState
import com.dimitriskikidis.fuelapp.presentation.map.MapUiState
import com.dimitriskikidis.fuelapp.presentation.map.MapViewModel
import com.dimitriskikidis.fuelapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val repository: FuelRepository,
    private val preferencesManager: PreferencesManager,
    private val userManager: UserManager
) : ViewModel() {

    sealed class UiEvent {
        object NavigateToMainNavGraph : UiEvent()
        data class ShowMessage(val message: String) : UiEvent()
        data class ShowMessageAndSignOut(val message: String) : UiEvent()
    }

    private val _state = MutableStateFlow(LoadingUiState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var dataState = DataState()

    init {
        viewModelScope.launch {
            val brandsDeferred = async { repository.getBrands() }
            val fuelTypesDeferred = async { repository.getFuelTypes() }

            val brands: List<Brand>
            val fuelTypes: List<FuelType>

            when (val response = brandsDeferred.await()) {
                is Resource.Success -> {
                    brands = response.data!!
                }
                is Resource.Error -> {
                    if (response.isUnauthorized) {
                        handleUnauthorizedError()
                        return@launch
                    }
//                    val message = response.message!!
                    val message = "An error has occurred."
                    _uiEvent.send(UiEvent.ShowMessage(message))
                    _state.update {
                        it.copy(isLoading = false)
                    }
                    return@launch
                }
            }

            when (val response = fuelTypesDeferred.await()) {
                is Resource.Success -> {
                    fuelTypes = response.data!!
                    dataState = dataState.copy(
                        brands = brands,
                        fuelTypes = fuelTypes
                    )
                }
                is Resource.Error -> {
                    if (response.isUnauthorized) {
                        handleUnauthorizedError()
                        return@launch
                    }
//                    val message = response.message!!
                    val message = "An error has occurred."
                    _uiEvent.send(UiEvent.ShowMessage(message))
                    _state.update {
                        it.copy(isLoading = false)
                    }
                    return@launch
                }
            }

            val currentBrandValues = preferencesManager.getBrandValues()?.map { it.toInt() }
            val brandValues =
                if (currentBrandValues.isNullOrEmpty()) {
                    brands.map { it.id.toString() }
                } else {
                    val currentBrandEntryValues =
                        preferencesManager.getBrandEntryValues()!!.map { it.toInt() }
                    brands.filter {
                        !currentBrandEntryValues.contains(it.id) || currentBrandValues.contains(it.id)
                    }.map { it.id.toString() }
                }
            preferencesManager.setBrandValues(brandValues.toSet())

            val brandEntries = brands.map { it.name }
            preferencesManager.setBrandEntries(brandEntries)

            val brandEntryValues = brands.map { it.id.toString() }
            preferencesManager.setBrandEntryValues(brandEntryValues)

            val fuelTypeEntries = fuelTypes.map { it.name }
            preferencesManager.setFuelTypeEntries(fuelTypeEntries)

            val fuelTypeEntryValues = fuelTypes.map { it.id.toString() }
            preferencesManager.setFuelTypeEntryValues(fuelTypeEntryValues)

            val currentFuelTypeValue = preferencesManager.getFuelTypeValue()
            if (currentFuelTypeValue.isNullOrEmpty() ||
                !fuelTypeEntryValues.contains(currentFuelTypeValue)
            ) {
                val fuelTypeValue = fuelTypeEntryValues.first()
                preferencesManager.setFuelTypeValue(fuelTypeValue)
            }

            _uiEvent.send(UiEvent.NavigateToMainNavGraph)
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
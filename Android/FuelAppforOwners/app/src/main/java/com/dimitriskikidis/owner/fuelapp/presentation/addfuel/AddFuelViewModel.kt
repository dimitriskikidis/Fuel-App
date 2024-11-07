package com.dimitriskikidis.owner.fuelapp.presentation.addfuel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskikidis.owner.fuelapp.data.remote.UserManager
import com.dimitriskikidis.owner.fuelapp.data.remote.requests.FuelCreateRequest
import com.dimitriskikidis.owner.fuelapp.domain.models.BrandFuel
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
class AddFuelViewModel @Inject constructor(
    private val repository: FuelRepository,
    private val userManager: UserManager
) : ViewModel() {

    sealed class UiEvent {
        object NavigateBackWithResult : UiEvent()
        data class ShowMessage(val message: String) : UiEvent()
        data class ShowMessageAndSignOut(val message: String) : UiEvent()
    }

    private val _state = MutableStateFlow(AddFuelUiState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var brandFuels: List<BrandFuel> = emptyList()
    private var selectedBrandFuelId: Int? = null
    var fuelTypeIds: List<Int>? = null
        private set

    var brandFuelNames: Array<String> = emptyArray()
        private set
    var selectedBrandFuelName: String? = null
        private set
    var price: String = ""
        private set

    fun onInitFuelTypeIds(fuelTypeIds: List<Int>) {
        this@AddFuelViewModel.fuelTypeIds = fuelTypeIds
        initData()
    }

    fun onBrandFuelChange(brandFuelName: String) {
        if (brandFuelName.isBlank()) return
        val selectedBrandFuel = brandFuels.first { it.name == brandFuelName }
        selectedBrandFuelId = selectedBrandFuel.id
        selectedBrandFuelName = selectedBrandFuel.name
    }

    fun onPriceChange(price: String) {
        this.price = price
    }

    fun onAdd() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    brandFuelError = null,
                    priceError = null,
                    isLoading = true
                )
            }

            var brandFuelError: String? = null
            if (selectedBrandFuelId == null) {
                brandFuelError = "Select a fuel."
            }

            var priceError: String? = null
            val fuelPriceRegex = Regex("[0-9]+\\.[0-9]{3}")
            val isValidPrice = fuelPriceRegex.matches(price)
            if (price.isBlank()) {
                priceError = "This field is required."
            } else if (!isValidPrice) {
                priceError = "The price is invalid."
            }

            val errors = listOf(brandFuelError, priceError)
            val hasErrors = errors.any { it != null }

            if (hasErrors) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        brandFuelError = brandFuelError,
                        priceError = priceError
                    )
                }
                return@launch
            }

            val fuelStationId = userManager.getFuelStationId()!!.toInt()
            val priceInt = price.filter { it.isDigit() }.toInt()
            val request = FuelCreateRequest(
                selectedBrandFuelId!!,
                priceInt
            )

            when (val response = repository.createFuel(fuelStationId, request)) {
                is Resource.Success -> {
                    _uiEvent.send(UiEvent.NavigateBackWithResult)
//                    _uiEvent.send(
//                        UiEvent.ShowMessage(
//                            title = "Success",
//                            message = "The fuel was added successfully."
//                        )
//                    )
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

    private fun initData() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val fuelStationId = userManager.getFuelStationId()!!.toInt()

            when (val response = repository.getBrandFuelsByFuelStationId(fuelStationId)) {
                is Resource.Success -> {
                    val brandFuels =
                        if (fuelTypeIds!!.isEmpty()) {
                            response.data!!
                        } else {
                            response.data!!
                                .filterNot { brandFuel: BrandFuel ->
                                    fuelTypeIds!!.any { it == brandFuel.fuelTypeId }
                                }
                        }
                    this@AddFuelViewModel.brandFuels = brandFuels
                    brandFuelNames = brandFuels
                        .map { it.name }
                        .sorted()
                        .toTypedArray()

                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
                is Resource.Error -> {
                    if (response.isUnauthorized) {
                        handleUnauthorizedError()
                        return@launch
                    }
                    val message = response.message!!
                    _uiEvent.send(UiEvent.ShowMessage(message))
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = message
                        )
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
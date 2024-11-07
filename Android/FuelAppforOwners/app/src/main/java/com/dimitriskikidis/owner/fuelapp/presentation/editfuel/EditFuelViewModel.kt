package com.dimitriskikidis.owner.fuelapp.presentation.editfuel

import android.icu.text.DecimalFormat
import android.icu.text.DecimalFormatSymbols
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskikidis.owner.fuelapp.data.remote.UserManager
import com.dimitriskikidis.owner.fuelapp.data.remote.requests.FuelUpdateRequest
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
import java.math.RoundingMode
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EditFuelViewModel @Inject constructor(
    private val repository: FuelRepository,
    private val userManager: UserManager
) : ViewModel() {

    sealed class UiEvent {
        object NavigateBackWithResult : UiEvent()
        data class ShowMessage(val message: String) : UiEvent()
        data class ShowMessageAndSignOut(val message: String) : UiEvent()
    }

    private val _state = MutableStateFlow(EditFuelUiState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val priceFormatter = DecimalFormat(
        "#.000",
        DecimalFormatSymbols.getInstance(Locale.ENGLISH)
    ).apply {
        roundingMode = RoundingMode.HALF_UP.ordinal
    }

    var currentFuel: Fuel? = null
        private set

    var name: String = ""
        private set
    var price: String = ""
        private set

    fun onInitFuel(fuel: Fuel) {
        currentFuel = fuel
        name = fuel.name
        price = priceFormatter.format(fuel.price / 1000.0)
    }

    fun onPriceChange(price: String) {
        this.price = price
    }

    fun onSave() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    priceError = null,
                    isLoading = true
                )
            }

            var priceError: String? = null
            val fuelPriceRegex = Regex("[0-9]+\\.[0-9]{3}")
            val isValidPrice = fuelPriceRegex.matches(price)
            if (price.isBlank()) {
                priceError = "This field is required."
            } else if (!isValidPrice) {
                priceError = "The price is invalid."
            }

            if (priceError != null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        priceError = priceError
                    )
                }
                return@launch
            }

            val fuelId = currentFuel!!.id
            val priceInt = price.filter { it.isDigit() }.toInt()
            val request = FuelUpdateRequest(priceInt)

            when (val response = repository.updateFuel(fuelId, request)) {
                is Resource.Success -> {
                    _uiEvent.send(UiEvent.NavigateBackWithResult)
//                    _uiEvent.send(
//                        UiEvent.ShowMessage(
//                            title = "Success",
//                            message = "The fuel price was updated successfully."
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
package com.dimitriskikidis.owner.fuelapp.presentation.editfuelstation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskikidis.owner.fuelapp.data.remote.UserManager
import com.dimitriskikidis.owner.fuelapp.data.remote.requests.FuelStationCreateUpdateRequest
import com.dimitriskikidis.owner.fuelapp.domain.models.Brand
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
class EditFuelStationViewModel @Inject constructor(
    private val repository: FuelRepository,
    private val userManager: UserManager
) : ViewModel() {

    sealed class UiEvent {
        object SetFragmentResult : UiEvent()
        data class ShowMessage(val title: String, val message: String) : UiEvent()
        data class ShowMessageAndSignOut(val message: String) : UiEvent()
    }

    private val _state = MutableStateFlow(EditFuelStationUiState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var brands: List<Brand> = emptyList()
    private var selectedBrandId: Int? = null

    private val _mapState = MutableStateFlow(EditFuelStationMapUiState())
    val mapState = _mapState.asStateFlow()

    var brandNames: Array<String> = emptyArray()
        private set
    var selectedBrandName: String? = null
        private set
    var name: String = ""
        private set
    var city: String = ""
        private set
    var address: String = ""
        private set
    var postalCode: String = ""
        private set
    var phoneNumber: String = ""
        private set
    var latitude: Double? = null
        private set
    var longitude: Double? = null
        private set

    init {
        viewModelScope.launch {
            initData()
        }
    }

    fun onBrandChange(brandName: String) {
        if (brandName.isBlank()) return
        val selectedBrand = brands.first { it.name == brandName }
        selectedBrandId = selectedBrand.id
        selectedBrandName = selectedBrand.name
    }

    fun onNameChange(name: String) {
        this.name = name
    }

    fun onCityChanged(city: String) {
        this.city = city
    }

    fun onAddressChange(address: String) {
        this.address = address
    }

    fun onPostalCodeChanged(postalCode: String) {
        this.postalCode = postalCode
    }

    fun onPhoneNumberChange(phoneNumber: String) {
        this.phoneNumber = phoneNumber
    }

    fun onLocationChange(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
        _mapState.update {
            it.copy(
                isVisible = true,
                latitude = latitude,
                longitude = longitude
            )
        }
    }

    fun onSave() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    brandError = null,
                    nameError = null,
                    cityError = null,
                    addressError = null,
                    postalCodeError = null,
                    phoneNumberError = null,
                    locationError = null
                )
            }

            var brandError: String? = null
            if (selectedBrandId == null) {
                brandError = "Select a brand."
            }

            var nameError: String? = null
            if (name.isBlank()) {
                nameError = "This field is required." // "The name cannot be empty."
            }

            var cityError: String? = null
            if (city.isBlank()) {
                cityError = "This field is required." // "The city cannot be empty."
            }

            var addressError: String? = null
            if (address.isBlank()) {
                addressError = "This field is required." // "The address cannot be empty."
            }

            var postalCodeError: String? = null
            val postalCodeRegex = Regex("[0-9]{5}")
            val isValidPostalCode = postalCodeRegex.matches(postalCode)
            if (postalCode.isBlank()) {
                postalCodeError = "This field is required." // "The postal code cannot be empty."
            } else if (!isValidPostalCode) {
                postalCodeError = "The postal code is invalid."
            }

            var phoneNumberError: String? = null
            val phoneRegex = Regex("69[0-9]{8}|2[0-9]{9}")
            val isValidPhoneNumber = phoneRegex.matches(phoneNumber)
            if (address.isBlank()) {
                phoneNumberError = "This field is required." // "The phone number cannot be empty."
            } else if (!isValidPhoneNumber) {
                phoneNumberError = "The phone number is invalid."
            }

            var locationError: String? = null
            if (latitude == null || longitude == null) {
                locationError = "Set a map location."
            }

            val errors = listOf(
                brandError, nameError, phoneNumberError, cityError, addressError,
                postalCodeError, locationError
            )
            val hasErrors = errors.any { it != null }

            if (hasErrors) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        brandError = brandError,
                        nameError = nameError,
                        cityError = cityError,
                        addressError = addressError,
                        postalCodeError = postalCodeError,
                        phoneNumberError = phoneNumberError,
                        locationError = locationError
                    )
                }
                return@launch
            }

            val request = FuelStationCreateUpdateRequest(
                selectedBrandId!!,
                latitude!!,
                longitude!!,
                name,
                city,
                address,
                postalCode,
                phoneNumber
            )

            val fuelStationId = userManager.getFuelStationId()?.toInt()

            if (fuelStationId == null) {
                val ownerId = userManager.getOwnerId()

                when (val response = repository.createFuelStation(ownerId, request)) {
                    is Resource.Success -> {
                        val fuelStationCreateResponse = response.data!!
                        val responseFuelStationId =
                            fuelStationCreateResponse.fuelStationId.toString()
                        userManager.setFuelStationId(responseFuelStationId)
                        _uiEvent.send(UiEvent.SetFragmentResult)
                        initData()
//                        _uiEvent.send(
//                            UiEvent.ShowMessage(
//                                title = "Success",
//                                message = "The fuel station was created successfully."
//                            )
//                        )
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
            } else {
                when (val response = repository.updateFuelStation(fuelStationId, request)) {
                    is Resource.Success -> {
                        initData()
//                        _uiEvent.send(
//                            UiEvent.ShowMessage(
//                                title = "Success",
//                                message = "The fuel station was updated successfully."
//                            )
//                        )
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
            }

            initData()
        }
    }

    private suspend fun initData() {
        _state.update {
            it.copy(isLoading = true)
        }

        when (val response = repository.getBrands()) {
            is Resource.Success -> {
                val brands = response.data!!
                brandNames = brands.map { it.name }.toTypedArray()
                this@EditFuelStationViewModel.brands = brands
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
                        error = message
                    )
                }
                return
            }
        }

        val hasFuelStation = userManager.getFuelStationId() != null
        if (!hasFuelStation) {
            _state.update {
                it.copy(isLoading = false)
            }
            return
        }
        val ownerId = userManager.getOwnerId()

        when (val response = repository.getFuelStationByOwnerId(ownerId)) {
            is Resource.Success -> {
                val fuelStation = response.data
                if (fuelStation == null) {
                    _state.update {
                        it.copy(isLoading = false)
                    }
                    return
                }

                selectedBrandId = fuelStation.brandId
                selectedBrandName = brands.first { it.id == selectedBrandId }.name
                name = fuelStation.name
                city = fuelStation.city
                address = fuelStation.address
                postalCode = fuelStation.postalCode
                phoneNumber = fuelStation.phoneNumber
                latitude = fuelStation.latitude
                longitude = fuelStation.longitude
                _mapState.update {
                    it.copy(
                        isVisible = true,
                        latitude = latitude,
                        longitude = longitude
                    )
                }
                _state.update {
                    it.copy(isLoading = false)
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
                        error = message
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
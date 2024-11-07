package com.dimitriskikidis.owner.fuelapp.presentation.editfuelstation

data class EditFuelStationUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val brandError: String? = null,
    val nameError: String? = null,
    val cityError: String? = null,
    val addressError: String? = null,
    val postalCodeError: String? = null,
    val phoneNumberError: String? = null,
    val locationError: String? = null
)

package com.dimitriskikidis.owner.fuelapp.presentation.addfuel

data class AddFuelUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val brandFuelError: String? = null,
    val priceError: String? = null
)

package com.dimitriskikidis.fuelapp.presentation.fuelstationdetails

import com.dimitriskikidis.fuelapp.domain.models.Fuel
import com.dimitriskikidis.fuelapp.domain.models.FuelStation

data class FuelStationDetailsUiState(
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val fuelStation: FuelStation? = null,
    val fuels: List<Fuel> = emptyList()
)

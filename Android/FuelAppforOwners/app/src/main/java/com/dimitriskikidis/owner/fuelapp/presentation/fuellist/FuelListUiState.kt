package com.dimitriskikidis.owner.fuelapp.presentation.fuellist

import com.dimitriskikidis.owner.fuelapp.domain.models.Fuel

data class FuelListUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val fuels: List<Fuel> = emptyList()
)

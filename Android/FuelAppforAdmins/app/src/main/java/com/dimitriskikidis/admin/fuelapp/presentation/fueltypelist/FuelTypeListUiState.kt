package com.dimitriskikidis.admin.fuelapp.presentation.fueltypelist

import com.dimitriskikidis.admin.fuelapp.domain.models.FuelType

data class FuelTypeListUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val fuelTypes: List<FuelType> = emptyList()
)

package com.dimitriskikidis.admin.fuelapp.presentation.brandfuellist

import com.dimitriskikidis.admin.fuelapp.domain.models.BrandFuel

data class BrandFuelListUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val brandFuels: List<BrandFuel> = emptyList()
)

package com.dimitriskikidis.fuelapp.presentation.fuellist

import com.dimitriskikidis.fuelapp.domain.models.FuelSearchResult

data class FuelListUiState(
    val sortedFuelSearchResults: List<FuelSearchResult> = emptyList(),
    val sortOrder: String = "sortByPrice"
)

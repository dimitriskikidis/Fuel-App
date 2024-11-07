package com.dimitriskikidis.fuelapp.presentation

import android.location.Location
import com.dimitriskikidis.fuelapp.domain.models.Brand
import com.dimitriskikidis.fuelapp.domain.models.FuelSearchResult
import com.dimitriskikidis.fuelapp.domain.models.FuelType

data class DataState(
    val brands: List<Brand> = emptyList(),
    val fuelTypes: List<FuelType> = emptyList(),
    val fuelSearchResults: List<FuelSearchResult> = emptyList(),
    val location: Location? = null
)

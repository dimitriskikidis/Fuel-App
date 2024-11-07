package com.dimitriskikidis.admin.fuelapp.presentation

import androidx.lifecycle.ViewModel
import com.dimitriskikidis.admin.fuelapp.domain.models.FuelType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FuelTypesViewModel @Inject constructor() : ViewModel() {

    var currentFuelType: FuelType? = null
    var fuelTypeNames: List<String> = emptyList()
        private set

    fun mapFuelTypeNames(fuelTypes: List<FuelType>) {
        fuelTypeNames = fuelTypes.map { it.name }
    }
}
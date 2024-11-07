package com.dimitriskikidis.admin.fuelapp.presentation.addeditfueltype

import com.dimitriskikidis.admin.fuelapp.domain.models.FuelType

sealed class AddEditFuelTypeEvent {
    data class OnInitFuelType(val currentFuelType: FuelType) : AddEditFuelTypeEvent()
    data class OnNameChange(val name: String) : AddEditFuelTypeEvent()
    data class OnAddSave(val fuelTypeNames: List<String>) : AddEditFuelTypeEvent()
}
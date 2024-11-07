package com.dimitriskikidis.admin.fuelapp.presentation.fueltypelist

import com.dimitriskikidis.admin.fuelapp.domain.models.FuelType

sealed class FuelTypeListEvent {
    object OnFuelTypeAddEditComplete : FuelTypeListEvent()
    data class OnFuelTypeDeleteConfirm(val fuelType: FuelType) : FuelTypeListEvent()
}

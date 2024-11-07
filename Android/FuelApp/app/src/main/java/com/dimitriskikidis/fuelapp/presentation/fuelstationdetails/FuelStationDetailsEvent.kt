package com.dimitriskikidis.fuelapp.presentation.fuelstationdetails

import com.dimitriskikidis.fuelapp.domain.models.FuelStation

sealed class FuelStationDetailsEvent {
    data class OnViewCreated(val fuelStation: FuelStation) : FuelStationDetailsEvent()
}

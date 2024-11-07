package com.dimitriskikidis.owner.fuelapp.presentation

import androidx.lifecycle.ViewModel
import com.dimitriskikidis.owner.fuelapp.domain.models.Fuel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FuelsViewModel @Inject constructor() : ViewModel() {

    lateinit var currentFuel: Fuel

    var fuelTypeIds: List<Int> = emptyList()
        private set

    fun mapFuelTypeIds(fuels: List<Fuel>) {
        fuelTypeIds = fuels.map { it.fuelTypeId }
    }
}
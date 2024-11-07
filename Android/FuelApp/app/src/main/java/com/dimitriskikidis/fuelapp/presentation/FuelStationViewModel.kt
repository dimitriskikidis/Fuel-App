package com.dimitriskikidis.fuelapp.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FuelStationViewModel @Inject constructor() : ViewModel() {

    var fuelStationId: Int? = null
}
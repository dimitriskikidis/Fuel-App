package com.dimitriskikidis.owner.fuelapp.presentation

import androidx.lifecycle.ViewModel
import javax.inject.Inject

class LocationViewModel @Inject constructor() : ViewModel() {

    var latitude: Double? = null
    var longitude: Double? = null
}
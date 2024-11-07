package com.dimitriskikidis.owner.fuelapp.presentation.picklocation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PickLocationViewModel @Inject constructor() : ViewModel() {

    var latitude: Double? = null
        private set
    var longitude: Double? = null
        private set

    fun updateLocationData(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }
}
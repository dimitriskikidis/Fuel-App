package com.dimitriskikidis.fuelapp.domain.location

import android.location.Location

interface LocationTracker {

    suspend fun getCurrentLocation(): Location?

    fun hasLocationPermission(): Boolean
}
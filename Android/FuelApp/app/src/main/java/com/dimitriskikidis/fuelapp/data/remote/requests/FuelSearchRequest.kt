package com.dimitriskikidis.fuelapp.data.remote.requests

data class FuelSearchRequest(
    val brandIds: List<Int>,
    val fuelTypeId: Int,
    val minLatitude: Double,
    val maxLatitude: Double,
    val minLongitude: Double,
    val maxLongitude: Double
)

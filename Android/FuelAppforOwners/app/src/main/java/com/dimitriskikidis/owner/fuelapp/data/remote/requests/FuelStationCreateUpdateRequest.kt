package com.dimitriskikidis.owner.fuelapp.data.remote.requests

data class FuelStationCreateUpdateRequest(
    val brandId: Int,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val city: String,
    val address: String,
    val postalCode: String,
    val phoneNumber: String
)

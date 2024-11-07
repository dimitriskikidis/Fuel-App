package com.dimitriskikidis.owner.fuelapp.domain.models

data class FuelStation(
    val id: Int,
    val brandId: Int,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val city: String,
    val address: String,
    val postalCode: String,
    val phoneNumber: String
)

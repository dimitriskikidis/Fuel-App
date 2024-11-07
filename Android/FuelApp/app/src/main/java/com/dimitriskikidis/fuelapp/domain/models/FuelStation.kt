package com.dimitriskikidis.fuelapp.domain.models

data class FuelStation(
    val id: Int,
    val brandId: Int,
    val latitude: Double,
    val longitude: Double,
    val rating: Float?,
    val reviewCount: Int?,
    val name: String,
    val city: String,
    val address: String,
    val postalCode: String,
    val phoneNumber: String
) {
    var distance: Double? = null
    var brand: Brand? = null
}

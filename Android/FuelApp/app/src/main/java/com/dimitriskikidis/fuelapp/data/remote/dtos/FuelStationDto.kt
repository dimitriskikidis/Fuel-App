package com.dimitriskikidis.fuelapp.data.remote.dtos

import com.squareup.moshi.Json

data class FuelStationDto(
    val id: Int,
    val brandId: Int,
    @field:Json(name = "brand")
    val brandDto: BrandDto?,
    val latitude: Double,
    val longitude: Double,
    val rating: Float?,
    val reviewCount: Int?,
    val name: String,
    val city: String,
    val address: String,
    val postalCode: String,
    val phoneNumber: String
)

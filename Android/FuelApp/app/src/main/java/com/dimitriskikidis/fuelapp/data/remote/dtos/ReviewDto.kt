package com.dimitriskikidis.fuelapp.data.remote.dtos

import com.squareup.moshi.Json

data class ReviewDto(
    val id: Int,
    val fuelStationId: Int?,
    @field:Json(name = "fuelStation")
    val fuelStationDto: FuelStationDto?,
    val consumerId: Int,
    val username: String?,
    val rating: Int,
    val text: String,
    val lastUpdate: String
)

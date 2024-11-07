package com.dimitriskikidis.fuelapp.data.remote.dtos

import com.squareup.moshi.Json

data class FuelSearchResultDto(
    @field:Json(name = "fuel")
    val fuelDto: FuelDto,
    @field:Json(name = "fuelStation")
    val fuelStationDto: FuelStationDto
)

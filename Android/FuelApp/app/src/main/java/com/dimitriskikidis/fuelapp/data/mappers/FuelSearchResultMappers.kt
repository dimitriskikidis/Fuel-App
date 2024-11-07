package com.dimitriskikidis.fuelapp.data.mappers

import com.dimitriskikidis.fuelapp.data.remote.dtos.FuelSearchResultDto
import com.dimitriskikidis.fuelapp.domain.models.FuelSearchResult

fun FuelSearchResultDto.toFuelSearchResult(): FuelSearchResult {
    return FuelSearchResult(
        fuel = fuelDto.toFuel(),
        fuelStation = fuelStationDto.toFuelStation()
    )
}
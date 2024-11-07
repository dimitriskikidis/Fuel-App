package com.dimitriskikidis.fuelapp.data.mappers

import com.dimitriskikidis.fuelapp.data.remote.dtos.FuelTypeDto
import com.dimitriskikidis.fuelapp.domain.models.FuelType

fun FuelTypeDto.toFuelType(): FuelType {
    return FuelType(
        id = id,
        name = name
    )
}
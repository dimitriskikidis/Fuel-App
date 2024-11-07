package com.dimitriskikidis.owner.fuelapp.data.mappers

import com.dimitriskikidis.owner.fuelapp.data.remote.dtos.FuelTypeDto
import com.dimitriskikidis.owner.fuelapp.domain.models.FuelType

fun FuelTypeDto.toFuelType(): FuelType {
    return FuelType(
        id = id,
        name = name
    )
}
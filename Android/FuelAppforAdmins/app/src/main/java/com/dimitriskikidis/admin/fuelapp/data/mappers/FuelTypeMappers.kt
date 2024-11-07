package com.dimitriskikidis.admin.fuelapp.data.mappers

import com.dimitriskikidis.admin.fuelapp.data.remote.dtos.FuelTypeDto
import com.dimitriskikidis.admin.fuelapp.domain.models.FuelType

fun FuelTypeDto.toFuelType(): FuelType {
    return FuelType(
        id = id,
        name = name
    )
}
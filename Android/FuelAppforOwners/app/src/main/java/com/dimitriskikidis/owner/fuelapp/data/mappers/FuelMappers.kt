package com.dimitriskikidis.owner.fuelapp.data.mappers

import com.dimitriskikidis.owner.fuelapp.data.remote.dtos.FuelDto
import com.dimitriskikidis.owner.fuelapp.domain.models.Fuel
import java.time.LocalDateTime

fun FuelDto.toFuel(): Fuel {
    return Fuel(
        id = id,
        fuelStationId = fuelStationId,
        fuelTypeId = fuelTypeId,
        name = name,
        price = price,
        lastUpdate = LocalDateTime.parse(lastUpdate)
    )
}
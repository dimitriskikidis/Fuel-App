package com.dimitriskikidis.fuelapp.data.mappers

import com.dimitriskikidis.fuelapp.data.remote.dtos.FuelDto
import com.dimitriskikidis.fuelapp.domain.models.Fuel
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun FuelDto.toFuel(): Fuel {
    return Fuel(
        id = id,
        fuelStationId = fuelStationId,
        fuelTypeId = fuelTypeId,
        name = name,
        price = price,
        lastUpdate = LocalDateTime.parse(lastUpdate)
            .atZone(ZoneOffset.UTC)
            .withZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime()
    )
}
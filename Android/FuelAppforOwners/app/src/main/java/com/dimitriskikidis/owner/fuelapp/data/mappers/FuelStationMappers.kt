package com.dimitriskikidis.owner.fuelapp.data.mappers

import com.dimitriskikidis.owner.fuelapp.data.remote.dtos.FuelStationDto
import com.dimitriskikidis.owner.fuelapp.domain.models.FuelStation

fun FuelStationDto.toFuelStation(): FuelStation {
    return FuelStation(
        id = id,
        brandId = brandId,
        latitude = latitude,
        longitude = longitude,
        city = city,
        name = name,
        address = address,
        postalCode = postalCode,
        phoneNumber = phoneNumber
    )
}
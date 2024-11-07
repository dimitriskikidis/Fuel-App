package com.dimitriskikidis.fuelapp.data.mappers

import com.dimitriskikidis.fuelapp.data.remote.dtos.FuelStationDto
import com.dimitriskikidis.fuelapp.domain.models.FuelStation

fun FuelStationDto.toFuelStation(): FuelStation {
    val fuelStation = FuelStation(
        id = id,
        brandId = brandId,
        latitude = latitude,
        longitude = longitude,
        rating = rating,
        reviewCount = reviewCount,
        name = name,
        city = city,
        address = address,
        postalCode = postalCode,
        phoneNumber = phoneNumber
    )

    brandDto?.let {
        fuelStation.brand = it.toBrand()
    }

    return fuelStation
}
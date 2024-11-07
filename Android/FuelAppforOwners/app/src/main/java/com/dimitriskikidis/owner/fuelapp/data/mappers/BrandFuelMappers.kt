package com.dimitriskikidis.owner.fuelapp.data.mappers

import com.dimitriskikidis.owner.fuelapp.data.remote.dtos.BrandFuelDto
import com.dimitriskikidis.owner.fuelapp.domain.models.BrandFuel

fun BrandFuelDto.toBrandFuel(): BrandFuel {
    return BrandFuel(
        id = id,
        brandId = brandId,
        fuelTypeId = fuelTypeId,
        name = name,
        isEnabled = isEnabled
    )
}
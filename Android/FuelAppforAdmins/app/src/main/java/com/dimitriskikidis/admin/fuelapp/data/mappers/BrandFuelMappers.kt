package com.dimitriskikidis.admin.fuelapp.data.mappers

import com.dimitriskikidis.admin.fuelapp.data.remote.dtos.BrandFuelDto
import com.dimitriskikidis.admin.fuelapp.domain.models.BrandFuel

fun BrandFuelDto.toBrandFuel(): BrandFuel {
    return BrandFuel(
        id = id,
        brandId = brandId,
        fuelTypeId = fuelTypeId,
        name = name,
        isEnabled = isEnabled
    )
}
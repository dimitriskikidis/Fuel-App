package com.dimitriskikidis.fuelapp.data.mappers

import com.dimitriskikidis.fuelapp.data.remote.dtos.BrandDto
import com.dimitriskikidis.fuelapp.domain.models.Brand

fun BrandDto.toBrand(): Brand {
    return Brand(
        id = id,
        name = name,
        iconBytes = iconBytes
    )
}
package com.dimitriskikidis.owner.fuelapp.data.mappers

import com.dimitriskikidis.owner.fuelapp.data.remote.dtos.BrandDto
import com.dimitriskikidis.owner.fuelapp.domain.models.Brand

fun BrandDto.toBrand(): Brand {
    return Brand(
        id = id,
        name = name,
        iconBytes = iconBytes
    )
}
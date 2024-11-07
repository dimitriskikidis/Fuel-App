package com.dimitriskikidis.admin.fuelapp.data.mappers

import com.dimitriskikidis.admin.fuelapp.data.remote.dtos.BrandDto
import com.dimitriskikidis.admin.fuelapp.domain.models.Brand

fun BrandDto.toBrand(): Brand {
    return Brand(
        id = id,
        name = name,
        iconBytes = iconBytes
    )
}
package com.dimitriskikidis.admin.fuelapp.data.remote.dtos

data class BrandFuelDto(
    val id: Int,
    val brandId: Int,
    val fuelTypeId: Int,
    val name: String,
    val isEnabled: Boolean
)

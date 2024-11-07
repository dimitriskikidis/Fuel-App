package com.dimitriskikidis.admin.fuelapp.domain.models

data class BrandFuel(
    val id: Int,
    val brandId: Int,
    val fuelTypeId: Int,
    val name: String,
    val isEnabled: Boolean
) {
    lateinit var fuelTypeName: String
}

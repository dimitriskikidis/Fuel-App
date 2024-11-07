package com.dimitriskikidis.owner.fuelapp.data.remote.dtos

data class FuelDto(
    val id: Int,
    val fuelStationId: Int,
    val fuelTypeId: Int,
    val name: String,
    val price: Int,
    val lastUpdate: String
)

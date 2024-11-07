package com.dimitriskikidis.fuelapp.domain.models

import java.time.LocalDateTime

data class Fuel(
    val id: Int,
    val fuelStationId: Int,
    val fuelTypeId: Int,
    val name: String,
    val price: Int,
    val lastUpdate: LocalDateTime
)

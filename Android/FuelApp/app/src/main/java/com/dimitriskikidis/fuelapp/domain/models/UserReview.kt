package com.dimitriskikidis.fuelapp.domain.models

import java.time.LocalDateTime

data class UserReview(
    val id: Int,
    val fuelStation: FuelStation,
    val rating: Int,
    val text: String,
    val lastUpdate: LocalDateTime
)

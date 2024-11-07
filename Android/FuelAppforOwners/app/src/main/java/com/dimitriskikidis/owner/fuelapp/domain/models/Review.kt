package com.dimitriskikidis.owner.fuelapp.domain.models

import java.time.LocalDateTime

data class Review(
    val id: Int,
    val username: String,
    val rating: Int,
    val text: String,
    val lastUpdate: LocalDateTime
)

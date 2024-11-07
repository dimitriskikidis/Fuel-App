package com.dimitriskikidis.fuelapp.domain.models

import java.time.LocalDateTime

data class Review(
    val id: Int,
    val consumerId: Int,
    val username: String,
    val rating: Int,
    val text: String,
    val lastUpdate: LocalDateTime
)

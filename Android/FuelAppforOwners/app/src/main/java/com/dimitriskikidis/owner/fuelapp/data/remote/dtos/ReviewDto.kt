package com.dimitriskikidis.owner.fuelapp.data.remote.dtos

data class ReviewDto(
    val id: Int,
    val username: String,
    val rating: Int,
    val text: String,
    val lastUpdate: String
)

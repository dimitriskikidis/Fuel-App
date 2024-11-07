package com.dimitriskikidis.fuelapp.data.remote.requests

data class ReviewCreateUpdateRequest(
    val rating: Int,
    val text: String
)

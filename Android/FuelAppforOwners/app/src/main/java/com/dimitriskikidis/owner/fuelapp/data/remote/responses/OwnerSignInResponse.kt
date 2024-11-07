package com.dimitriskikidis.owner.fuelapp.data.remote.responses

data class OwnerSignInResponse(
    val accessToken: String,
    val ownerId: Int,
    val firstName: String,
    val lastName: String,
    val fuelStationId: Int?
)

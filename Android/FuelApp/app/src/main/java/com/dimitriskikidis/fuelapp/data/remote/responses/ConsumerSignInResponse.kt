package com.dimitriskikidis.fuelapp.data.remote.responses

data class ConsumerSignInResponse(
    val accessToken: String,
    val consumerId: Int,
    val username: String
)

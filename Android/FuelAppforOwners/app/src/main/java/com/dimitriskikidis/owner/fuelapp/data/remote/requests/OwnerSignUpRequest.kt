package com.dimitriskikidis.owner.fuelapp.data.remote.requests

data class OwnerSignUpRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)

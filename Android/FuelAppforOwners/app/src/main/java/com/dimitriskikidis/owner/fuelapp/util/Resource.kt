package com.dimitriskikidis.owner.fuelapp.util

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T?) : Resource<T>(data, null)
    class Error<T>(message: String, val isUnauthorized: Boolean) : Resource<T>(null, message)
}

package com.dimitriskikidis.fuelapp.presentation.signup

data class SignUpUiState(
    val usernameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false
)

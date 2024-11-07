package com.dimitriskikidis.admin.fuelapp.presentation.signup

data class SignUpUiState(
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false
)

package com.dimitriskikidis.owner.fuelapp.presentation.signup

data class SignUpUiState(
    val isLoading: Boolean = false,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

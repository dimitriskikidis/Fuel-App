package com.dimitriskikidis.admin.fuelapp.presentation.signup

sealed class SignUpEvent {
    data class OnEmailChange(val email: String) : SignUpEvent()
    data class OnPasswordChange(val password: String) : SignUpEvent()
    data class OnConfirmPasswordChange(val confirmPassword: String) : SignUpEvent()
    object OnSignUp : SignUpEvent()
}
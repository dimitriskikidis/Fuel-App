package com.dimitriskikidis.admin.fuelapp.presentation.signin

sealed class SignInEvent {
    data class OnEmailChange(val email: String) : SignInEvent()
    data class OnPasswordChange(val password: String) : SignInEvent()
    object OnSignIn : SignInEvent()
}

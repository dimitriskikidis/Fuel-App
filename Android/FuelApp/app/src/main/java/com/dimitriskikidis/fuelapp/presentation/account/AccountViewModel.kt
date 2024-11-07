package com.dimitriskikidis.fuelapp.presentation.account

import androidx.lifecycle.ViewModel
import com.dimitriskikidis.fuelapp.data.remote.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val userManager: UserManager
) : ViewModel() {

    val username: String = userManager.getUsername()
    val email: String = userManager.getEmail()

    fun onSignOut() {
        userManager.clear()
    }
}
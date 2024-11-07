package com.dimitriskikidis.owner.fuelapp.presentation.account

import androidx.lifecycle.ViewModel
import com.dimitriskikidis.owner.fuelapp.data.remote.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val userManager: UserManager
) : ViewModel() {

    var fullName: String = ""
        private set
    var email: String = ""
        private set

    init {
        val firstName = userManager.getFirstName()
        val lastName = userManager.getLastName()
        fullName = "$firstName $lastName"
        email = userManager.getEmail()
    }

    fun onSignOut() {
        userManager.clear()
    }
}
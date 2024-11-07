package com.dimitriskikidis.admin.fuelapp.presentation.account

import androidx.lifecycle.ViewModel
import com.dimitriskikidis.admin.fuelapp.data.remote.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val userManager: UserManager
) : ViewModel() {

    var email: String = ""
        private set

    init {
        email = userManager.getEmail()
    }

    fun onSignOut() {
        userManager.clear()
    }
}
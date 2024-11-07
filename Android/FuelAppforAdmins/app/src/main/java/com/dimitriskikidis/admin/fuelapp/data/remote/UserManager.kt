package com.dimitriskikidis.admin.fuelapp.data.remote

import android.content.Context
import androidx.preference.PreferenceManager

class UserManager(context: Context) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val editor = sharedPreferences.edit()

    fun getAccessToken(): String? {
        return sharedPreferences.getString(ACCESS_TOKEN, null)
    }

    fun setAccessToken(accessToken: String) {
        editor
            .putString(ACCESS_TOKEN, accessToken)
            .apply()
    }

    fun getEmail(): String {
        return sharedPreferences.getString(EMAIL, null)!!
    }

    fun setEmail(email: String) {
        editor
            .putString(EMAIL, email)
            .apply()
    }

    fun clear() {
        editor
            .clear()
            .apply()
    }

    companion object {
        const val ACCESS_TOKEN = "access_token"
        const val EMAIL = "email"
    }
}
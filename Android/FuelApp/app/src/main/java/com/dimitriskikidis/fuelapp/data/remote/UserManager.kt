package com.dimitriskikidis.fuelapp.data.remote

import android.content.Context
import androidx.preference.PreferenceManager

class UserManager(context: Context) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val editor = sharedPreferences.edit()

    fun setUserData(
        accessToken: String,
        consumerId: Int,
        email: String,
        username: String
    ) {
        editor
            .putString(ACCESS_TOKEN, accessToken)
            .putInt(CONSUMER_ID, consumerId)
            .putString(EMAIL, email)
            .putString(USERNAME, username)
            .apply()
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString(ACCESS_TOKEN, null)
    }

    fun getConsumerId(): Int {
        return sharedPreferences.getInt(CONSUMER_ID, 0)
    }

    fun getEmail(): String {
        return sharedPreferences.getString(EMAIL, null)!!
    }

    fun getUsername(): String {
        return sharedPreferences.getString(USERNAME, null)!!
    }

    fun clear() {
        editor
            .clear()
            .apply()
    }

    companion object {
        const val ACCESS_TOKEN = "access_token"
        const val CONSUMER_ID = "consumer_id"
        const val EMAIL = "email"
        const val USERNAME = "username"
    }
}
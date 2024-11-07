package com.dimitriskikidis.owner.fuelapp.data.remote

import android.content.Context
import androidx.preference.PreferenceManager

class UserManager(context: Context) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val editor = sharedPreferences.edit()

    fun setData(
        accessToken: String,
        ownerId: Int,
        email: String,
        firstName: String,
        lastName: String
    ) {
        editor
            .putString(ACCESS_TOKEN, accessToken)
            .putInt(OWNER_ID, ownerId)
            .putString(EMAIL, email)
            .putString(FIRST_NAME, firstName)
            .putString(LAST_NAME, lastName)
            .apply()
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString(ACCESS_TOKEN, null)
    }

    fun getOwnerId(): Int {
        return sharedPreferences.getInt(OWNER_ID, 0)
    }

    fun getEmail(): String {
        return sharedPreferences.getString(EMAIL, null)!!
    }

    fun getFirstName(): String {
        return sharedPreferences.getString(FIRST_NAME, null)!!
    }

    fun getLastName(): String {
        return sharedPreferences.getString(LAST_NAME, null)!!
    }

    fun getFuelStationId(): String? {
        return sharedPreferences.getString(FUEL_STATION_ID, null)
    }

    fun setFuelStationId(fuelStationId: String) {
        editor
            .putString(FUEL_STATION_ID, fuelStationId)
            .apply()
    }

    fun clear() {
        editor
            .clear()
            .apply()
    }

    companion object {
        const val ACCESS_TOKEN = "access_token"
        const val OWNER_ID = "owner_id"
        const val EMAIL = "email"
        const val FIRST_NAME = "first_name"
        const val LAST_NAME = "last_name"
        const val FUEL_STATION_ID = "fuel_station_id"
    }
}
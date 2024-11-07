package com.dimitriskikidis.fuelapp.data.preferences

import android.content.Context
import androidx.preference.PreferenceManager

class PreferencesManager(context: Context) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val editor = sharedPreferences.edit()

    fun getUserId(): String? {
        return sharedPreferences.getString("userId", null)
    }

    fun setUserId(userId: String) {
        editor
            .putString("userId", userId)
            .apply()
    }

    fun getBrandEntries(): List<String>? {
        val str = sharedPreferences.getString("brandEntries", null)
        return str?.split(';')
    }

    fun setBrandEntries(brandEntries: List<String>) {
        editor
            .putString("brandEntries", brandEntries.joinToString(";"))
            .apply()
    }

    fun getBrandEntryValues(): List<String>? {
        val str = sharedPreferences.getString("brandEntryValues", null)
        return str?.split(';')
    }

    fun setBrandEntryValues(brandEntryValues: List<String>) {
        editor
            .putString("brandEntryValues", brandEntryValues.joinToString(";"))
            .apply()
    }

    fun getBrandValues(): Set<String>? {
        return sharedPreferences.getStringSet("brandValues", null)
    }

    fun setBrandValues(brandValues: Set<String>) {
        editor
            .putStringSet("brandValues", brandValues)
            .apply()
    }

    fun getFuelTypeEntries(): List<String>? {
        val str = sharedPreferences.getString("fuelTypeEntries", null)
        return str?.split(';')
    }

    fun setFuelTypeEntries(fuelTypeEntries: List<String>) {
        editor
            .putString("fuelTypeEntries", fuelTypeEntries.joinToString(";"))
            .apply()
    }

    fun getFuelTypeEntryValues(): List<String>? {
        val str = sharedPreferences.getString("fuelTypeEntryValues", null)
        return str?.split(';')
    }

    fun setFuelTypeEntryValues(fuelTypeEntryValues: List<String>) {
        editor
            .putString("fuelTypeEntryValues", fuelTypeEntryValues.joinToString(";"))
            .apply()
    }

    fun getFuelTypeValue(): String? {
        return sharedPreferences.getString("fuelTypeValue", null)
    }

    fun setFuelTypeValue(fuelTypeValue: String) {
        editor
            .putString("fuelTypeValue", fuelTypeValue)
            .apply()
    }

    fun setSortOrder(sortOrder: String) {
        editor
            .putString("sortOrder", sortOrder)
            .apply()
    }

    fun getSortOrder(): String? {
        return sharedPreferences.getString("sortOrder", null)
    }
}
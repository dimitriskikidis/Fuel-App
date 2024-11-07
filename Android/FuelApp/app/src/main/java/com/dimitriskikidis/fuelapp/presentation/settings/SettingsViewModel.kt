package com.dimitriskikidis.fuelapp.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.preference.MultiSelectListPreference
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.Preference.SummaryProvider
import com.dimitriskikidis.fuelapp.data.preferences.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val brandsPreferenceChangeListener = object : OnPreferenceChangeListener {
        override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
            return newValue != emptySet<String>()
        }
    }

    val brandsPreferenceSummaryProvider = SummaryProvider<MultiSelectListPreference> {
        if (it.values.size == it.entries.size) {
            "All"
        } else {
            "${it.values.size} selected"
        }
    }

    fun getPreferencesManager(): PreferencesManager {
        return preferencesManager
    }
}
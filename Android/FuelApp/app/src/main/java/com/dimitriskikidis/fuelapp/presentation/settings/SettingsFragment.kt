package com.dimitriskikidis.fuelapp.presentation.settings

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.preference.ListPreference
import androidx.preference.MultiSelectListPreference
import androidx.preference.PreferenceFragmentCompat
import com.dimitriskikidis.fuelapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)
        val pm = viewModel.getPreferencesManager()

        val brandsPreference = MultiSelectListPreference(context).apply {
            key = "brandValues"
            title = "Brands"
            entries = pm.getBrandEntries()?.toTypedArray()
            entryValues = pm.getBrandEntryValues()?.toTypedArray()
            summaryProvider = viewModel.brandsPreferenceSummaryProvider
            onPreferenceChangeListener = viewModel.brandsPreferenceChangeListener
        }
        brandsPreference.setIcon(R.drawable.ic_arrow_forward_24)

        val fuelTypePreference = ListPreference(context).apply {
            key = "fuelTypeValue"
            title = "Fuel type"
            entries = pm.getFuelTypeEntries()?.toTypedArray()
            entryValues = pm.getFuelTypeEntryValues()?.toTypedArray()
            summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        }
        fuelTypePreference.setIcon(R.drawable.ic_arrow_forward_24)

        screen.addPreference(brandsPreference)
        screen.addPreference(fuelTypePreference)

        preferenceScreen = screen
    }
}
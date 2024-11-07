package com.dimitriskikidis.fuelapp.presentation.fuellist

import android.location.Location
import androidx.lifecycle.ViewModel
import com.dimitriskikidis.fuelapp.data.preferences.PreferencesManager
import com.dimitriskikidis.fuelapp.domain.models.FuelSearchResult
import com.dimitriskikidis.fuelapp.presentation.DataState
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class FuelListViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _state = MutableStateFlow(FuelListUiState())
    val state = _state.asStateFlow()

    private var fuelSearchResults: List<FuelSearchResult> = emptyList()
    var location: Location? = null
        private set

    fun onInitDataState(dataState: DataState) {
        location = dataState.location
        fuelSearchResults = dataState.fuelSearchResults
        if (location != null) {
            val location = this.location!!

            fuelSearchResults.forEach {
                val distance = SphericalUtil.computeDistanceBetween(
                    LatLng(
                        location.latitude,
                        location.longitude
                    ),
                    LatLng(
                        it.fuelStation.latitude,
                        it.fuelStation.longitude
                    )
                ) / 1000.0
                it.fuelStation.distance = distance
            }
        }
        val sortOrder = getSortOrder()
        val sortedFuelSearchResults = sortResults(sortOrder)
        _state.update {
            it.copy(sortedFuelSearchResults = sortedFuelSearchResults)
        }
    }

    fun onSortOrderChange(sortOrder: String) {
        val sortedFuelSearchResults = sortResults(sortOrder)
        _state.update {
            it.copy(sortedFuelSearchResults = sortedFuelSearchResults)
        }
        preferencesManager.setSortOrder(sortOrder)
    }

    fun getSortOrder(): String {
        val sortOrder = preferencesManager.getSortOrder()
        return if (sortOrder == "sortByDistance" && location != null) {
            "sortByDistance"
        } else {
            "sortByPrice"
        }
    }

    private fun sortResults(sortOrder: String): List<FuelSearchResult> {
        return if (sortOrder == "sortByPrice") {
            fuelSearchResults.sortedBy { it.fuel.price }
        } else {
            fuelSearchResults.sortedBy { it.fuelStation.distance }
        }
    }
}
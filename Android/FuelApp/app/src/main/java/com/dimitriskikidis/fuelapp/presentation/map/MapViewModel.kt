package com.dimitriskikidis.fuelapp.presentation.map

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskikidis.fuelapp.data.preferences.PreferencesManager
import com.dimitriskikidis.fuelapp.data.remote.UserManager
import com.dimitriskikidis.fuelapp.data.remote.requests.FuelSearchRequest
import com.dimitriskikidis.fuelapp.domain.location.LocationTracker
import com.dimitriskikidis.fuelapp.domain.models.FuelSearchResult
import com.dimitriskikidis.fuelapp.domain.models.FuelStation
import com.dimitriskikidis.fuelapp.domain.repository.FuelRepository
import com.dimitriskikidis.fuelapp.presentation.DataState
import com.dimitriskikidis.fuelapp.util.Resource
import com.dimitriskikidis.fuelapp.util.px
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: FuelRepository,
    private val preferencesManager: PreferencesManager,
    private val locationTracker: LocationTracker,
    private val userManager: UserManager
) : ViewModel() {

    sealed class UiEvent {
        object NoResults : UiEvent()
        data class ShowMessage(val title: String, val message: String) : UiEvent()
        data class AnimateCameraToLocation(val latLng: LatLng, val zoom: Float) : UiEvent()
        data class AnimateCameraToMarkers(val latLngBounds: LatLngBounds, val padding: Int) :
            UiEvent()

        data class NavigateToFuelStationDetailsFragment(val fuelStation: FuelStation) : UiEvent()
        data class ShowMessageAndSignOut(val message: String) : UiEvent()
    }

    private val _state = MutableStateFlow(MapUiState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _fuelSearchResults = MutableStateFlow(emptyList<FuelSearchResult>())
    val fuelSearchResults = _fuelSearchResults.asStateFlow()

    private val _locationState = MutableStateFlow<Location?>(null)
    val locationState = _locationState.asStateFlow()

    private val _dataState = MutableStateFlow(DataState())
    val dataState = _dataState.asStateFlow()

    var cameraPosition: CameraPosition = CameraPosition.fromLatLngZoom(
        LatLng(38.505, 24.071),
        6f
    )

    init {
        viewModelScope.launch {
            if (locationTracker.hasLocationPermission()) {
                getLocation()
            }
        }
    }

    fun onInitDataState(dataState: DataState) {
        _dataState.update { dataState }
    }

    fun onSearch(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            val brandIds =
                preferencesManager.getBrandValues()!!.toList().map { it.toInt() }.sorted()

            val fuelTypeId = preferencesManager.getFuelTypeValue()!!.toInt()

            val request = FuelSearchRequest(
                brandIds = brandIds,
                fuelTypeId = fuelTypeId,
                minLatitude = latitude - 0.35,
                maxLatitude = latitude + 0.35,
                minLongitude = longitude - 0.25,
                maxLongitude = longitude + 0.25
            )

            when (val response = repository.searchFuels(request)) {
                is Resource.Success -> {
                    val fuelSearchResults = response.data!!
                    fuelSearchResults.forEach {
                        val brand = dataState.value.brands
                            .first { brand -> brand.id == it.fuelStation.brandId }
                        it.fuelStation.brand = brand
                    }

                    if (fuelSearchResults.isNotEmpty()) {
                        val builder = LatLngBounds.Builder()
                        fuelSearchResults.forEach {
                            builder.include(
                                LatLng(
                                    it.fuelStation.latitude,
                                    it.fuelStation.longitude
                                )
                            )
                        }

                        _uiEvent.send(UiEvent.AnimateCameraToMarkers(builder.build(), 40.px))
                    } else {
                        _uiEvent.send(UiEvent.NoResults)
                    }

                    _dataState.update {
                        it.copy(fuelSearchResults = fuelSearchResults)
                    }

                    _fuelSearchResults.update { fuelSearchResults }
                }
                is Resource.Error -> {
                    if (response.isUnauthorized) {
                        handleUnauthorizedError()
                        return@launch
                    }
                    val message = "An error has occurred."
                    _uiEvent.send(
                        UiEvent.ShowMessage(
                            title = "Error",
                            message = message
                        )
                    )
                }
            }

            _state.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun onMarkerClick(fuelStationId: Int) {
        viewModelScope.launch {
            val currentFuelStation =
                fuelSearchResults.value
                    .first { it.fuelStation.id == fuelStationId }
                    .fuelStation

            _uiEvent.send(
                UiEvent.NavigateToFuelStationDetailsFragment(currentFuelStation)
            )
        }
    }

    fun onRequestLocation() {
        viewModelScope.launch {
            getLocation()
        }
    }

    private suspend fun getLocation() {
        locationTracker.getCurrentLocation()?.let { location ->
            _dataState.update {
                it.copy(location = location)
            }
            _locationState.update { location }
            _uiEvent.send(
                UiEvent.AnimateCameraToLocation(
                    LatLng(location.latitude, location.longitude),
                    14f
                )
            )
        }
    }

    private suspend fun handleUnauthorizedError() {
        userManager.clear()
        _uiEvent.send(
            UiEvent.ShowMessageAndSignOut(
                "Your session has expired. You must sign in to continue" +
                        " using the app."
            )
        )
    }
}
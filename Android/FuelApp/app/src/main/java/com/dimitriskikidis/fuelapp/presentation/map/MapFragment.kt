package com.dimitriskikidis.fuelapp.presentation.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.dimitriskikidis.fuelapp.R
import com.dimitriskikidis.fuelapp.databinding.CustomMarkerIconBinding
import com.dimitriskikidis.fuelapp.databinding.FragmentMapBinding
import com.dimitriskikidis.fuelapp.presentation.DataViewModel
import com.dimitriskikidis.fuelapp.presentation.FuelStationViewModel
import com.dimitriskikidis.fuelapp.util.MarkerIconGenerator
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.RoundingMode
import kotlin.math.roundToInt

@AndroidEntryPoint
class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MapViewModel by hiltNavGraphViewModels(R.id.main_nav_graph)
    private val dataViewModel: DataViewModel by hiltNavGraphViewModels(R.id.main_nav_graph)
    private val fuelStationViewModel: FuelStationViewModel by hiltNavGraphViewModels(R.id.main_nav_graph)

    private lateinit var googleMap: GoogleMap
    private val locationRequestPermissionLauncher = initPermissionLauncher()
    private val locationSource = initLocationSource()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.onInitDataState(dataViewModel.state)

        val priceFormatter = DecimalFormat("0.000").apply {
            roundingMode = RoundingMode.HALF_UP.ordinal
        }

        binding.apply {
            val mapFragment =
                childFragmentManager.findFragmentById(R.id.fcv_map) as SupportMapFragment?

            mapFragment?.getMapAsync {
                googleMap = it
                setupMap()

                btnSearch.setOnClickListener {
                    googleMap.cameraPosition.target.apply {
                        viewModel.onSearch(latitude, longitude)
                    }
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.fuelSearchResults.collectLatest { fuelSearchResults ->
                            googleMap.clear()
                            if (fuelSearchResults.isEmpty()) {
                                return@collectLatest
                            }

                            val customMarkerIcon =
                                View.inflate(requireContext(), R.layout.custom_marker_icon, null)

                            val customMarkerIconBinding =
                                CustomMarkerIconBinding.bind(customMarkerIcon)

                            fuelSearchResults.forEach { fuelSearchResult ->
                                val fuel = fuelSearchResult.fuel
                                val fuelStation = fuelSearchResult.fuelStation
                                val brand = viewModel.dataState.value.brands
                                    .first { brand -> brand.id == fuelStation.brandId }

                                customMarkerIconBinding.apply {
                                    ivBrand.setImageBitmap(brand.iconBitmap)
                                    val hasReviews = fuelStation.rating != null
                                    pbRating.isVisible = hasReviews
                                    if (hasReviews) {
                                        pbRating.progress = (fuelStation.rating!! * 10).roundToInt()
                                    }
                                    tvFuelPrice.text = priceFormatter.format(fuel.price / 1000.0)
                                }

                                val marker = googleMap.addMarker(
                                    MarkerOptions()
                                        .position(
                                            LatLng(
                                                fuelStation.latitude,
                                                fuelStation.longitude
                                            )
                                        )
                                        .icon(
                                            BitmapDescriptorFactory.fromBitmap(
                                                MarkerIconGenerator.makeMarkerIcon(customMarkerIcon)
                                            )
                                        )
                                )

                                marker?.tag = fuelStation.id
                            }
                        }
                    }
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.uiEvent.collect { event ->
                            when (event) {
                                is MapViewModel.UiEvent.NoResults -> {
                                    MaterialAlertDialogBuilder(requireContext())
                                        .setMessage("No results.")
                                        .setPositiveButton("OK") { _, _ -> }
                                        .create()
                                        .show()
                                }
                                is MapViewModel.UiEvent.ShowMessage -> {
                                    MaterialAlertDialogBuilder(requireContext())
                                        .setTitle(event.title)
                                        .setMessage(event.message)
                                        .setPositiveButton("OK") { _, _ -> }
                                        .create()
                                        .show()
                                }
                                is MapViewModel.UiEvent.AnimateCameraToLocation -> {
                                    googleMap.stopAnimation()
                                    googleMap.animateCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            event.latLng,
                                            event.zoom
                                        )
                                    )
                                }
                                is MapViewModel.UiEvent.AnimateCameraToMarkers -> {
                                    googleMap.stopAnimation()
                                    googleMap.animateCamera(
                                        CameraUpdateFactory.newLatLngBounds(
                                            event.latLngBounds,
                                            event.padding
                                        )
                                    )
                                }
                                is MapViewModel.UiEvent.NavigateToFuelStationDetailsFragment -> {
                                    fuelStationViewModel.fuelStationId = event.fuelStation.id
                                    findNavController().navigate(
                                        R.id.action_mapFragment_to_fuelStationDetailsFragment
                                    )
                                }
                                is MapViewModel.UiEvent.ShowMessageAndSignOut -> {
                                    MaterialAlertDialogBuilder(requireContext())
                                        .setMessage(event.message)
                                        .setPositiveButton("OK") { _, _ -> }
                                        .setOnDismissListener {
                                            findNavController().navigate(
                                                R.id.action_global_signInFragment
                                            )
                                        }
                                        .create()
                                        .show()
                                }
                            }
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collectLatest { state ->
                        progressBar.isVisible = state.isLoading
                        btnSearch.isVisible = !state.isLoading
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.dataState.collectLatest { state ->
                        dataViewModel.state = state
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initPermissionLauncher(): ActivityResultLauncher<Array<String>> {
        return registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
                || permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
            ) {
                googleMap.apply {
                    setLocationSource(locationSource)
                    isMyLocationEnabled = true
                }
                viewModel.onRequestLocation()
            }
        }
    }

    private fun initLocationSource(): LocationSource {
        return object : LocationSource {
            var job: Job? = null

            override fun activate(listener: OnLocationChangedListener) {
                job = viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.locationState.collectLatest {
                            if (it != null) {
                                listener.onLocationChanged(it)
                            }
                        }
                    }
                }
            }

            override fun deactivate() {
                job?.cancel()
                job = null
            }
        }
    }

    @SuppressLint("PotentialBehaviorOverride", "MissingPermission")
    private fun setupMap() {
        googleMap.apply {
            moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    viewModel.cameraPosition.target,
                    viewModel.cameraPosition.zoom
                )
            )

            isBuildingsEnabled = false
            isIndoorEnabled = false

            uiSettings.apply {
                isZoomControlsEnabled = true
                isRotateGesturesEnabled = false
                isTiltGesturesEnabled = false
                isMapToolbarEnabled = false
            }

            setOnMarkerClickListener {
                val fuelStationId = it.tag as Int
                viewModel.onMarkerClick(fuelStationId)
                true
            }

            setOnCameraIdleListener {
                viewModel.cameraPosition = CameraPosition.fromLatLngZoom(
                    cameraPosition.target,
                    cameraPosition.zoom
                )
            }
        }

        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!(hasAccessCoarseLocationPermission && hasAccessFineLocationPermission)) {
            locationRequestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }

        if (!(hasAccessCoarseLocationPermission || hasAccessFineLocationPermission)) {
            return
        }

        googleMap.apply {
            setLocationSource(locationSource)
            isMyLocationEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
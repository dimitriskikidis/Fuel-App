package com.dimitriskikidis.owner.fuelapp.presentation.picklocation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResult
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.dimitriskikidis.owner.fuelapp.R
import com.dimitriskikidis.owner.fuelapp.databinding.FragmentPickLocationBinding
import com.dimitriskikidis.owner.fuelapp.presentation.LocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PickLocationFragment : Fragment() {

    private var _binding: FragmentPickLocationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PickLocationViewModel by viewModels()
    private val locationViewModel: LocationViewModel by hiltNavGraphViewModels(R.id.editFuelStationFragment)

    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPickLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.latitude == null) {
            if (locationViewModel.latitude != null) {
                viewModel.updateLocationData(
                    locationViewModel.latitude!!,
                    locationViewModel.longitude!!
                )
            }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.fcv_map) as SupportMapFragment?
        mapFragment?.getMapAsync {
            googleMap = it
            googleMap.apply {
                isBuildingsEnabled = false
                isIndoorEnabled = false

                uiSettings.apply {
                    isZoomControlsEnabled = true
                    isRotateGesturesEnabled = false
                    isTiltGesturesEnabled = false
                    isMapToolbarEnabled = false
                }

                if (viewModel.latitude == null) {
                    moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(38.505, 24.071), 6f
                        )
                    )
                } else {
                    val latitude = viewModel.latitude!!
                    val longitude = viewModel.longitude!!
                    moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                latitude,
                                longitude
                            ),
                            16f
                        )
                    )

                    googleMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(latitude, longitude))
                            .icon(BitmapDescriptorFactory.defaultMarker())
                    )
                }
            }

            binding.apply {
                btnSave.isVisible = true
                btnSave.setOnClickListener {
                    googleMap.cameraPosition.target.apply {
                        viewModel.updateLocationData(latitude, longitude)

                        googleMap.clear()
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(latitude, longitude))
                                .icon(BitmapDescriptorFactory.defaultMarker())
                        )

                        clearFragmentResult("PickLocationFragment")
                        setFragmentResult(
                            "PickLocationFragment",
                            bundleOf(
                                "latitude" to latitude,
                                "longitude" to longitude
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
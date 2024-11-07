package com.dimitriskikidis.fuelapp.presentation.fuelstationdetails

import android.icu.text.DecimalFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.dimitriskikidis.fuelapp.R
import com.dimitriskikidis.fuelapp.databinding.FragmentFuelStationDetailsBinding
import com.dimitriskikidis.fuelapp.presentation.FuelStationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.RoundingMode

@AndroidEntryPoint
class FuelStationDetailsFragment : Fragment() {

    private var _binding: FragmentFuelStationDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FuelStationDetailsViewModel by viewModels()
    private val fuelStationViewModel: FuelStationViewModel by hiltNavGraphViewModels(R.id.main_nav_graph)

    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFuelStationDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.fuelStationId == null) {
            viewModel.onInitFuelStationId(fuelStationViewModel.fuelStationId!!)
        }

        val fuelAdapter = FuelAdapter()
        val ratingFormatter = DecimalFormat("#.0").apply {
            roundingMode = RoundingMode.HALF_UP.ordinal
        }

        binding.apply {
            setupMap()

            rvFuels.apply {
                adapter = fuelAdapter
            }

            btnReviews.setOnClickListener {
                findNavController().navigate(
                    R.id.action_fuelStationDetailsFragment_to_reviewListFragment
                )
            }

            setFragmentResultListener("UserReviewUpdate@RLF") { requestKey: String, bundle: Bundle ->
                val parent = findNavController().previousBackStackEntry!!.destination.id
                if (parent == R.id.userReviewListFragment) {
                    setFragmentResult("UserReviewUpdate@FSDF", bundleOf())
                }
                viewModel.onUserReviewUpdate()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collectLatest { state ->
                        if (!state.isLoading && !state.hasError) {
                            state.fuelStation?.let {
                                val brand = it.brand!!
                                ivBrandIcon.setImageBitmap(brand.iconBitmap)
                                tvBrandName.text = brand.name
                                tvName.text = it.name
                                rbRating.rating = it.rating ?: 0f
                                val summaryText =
                                    if (it.rating != null) {
                                        "${ratingFormatter.format(it.rating)} (${it.reviewCount!!})"
                                    } else {
                                        ""
                                    }
                                tvReviewSummary.text = summaryText
                                tvAddress.text = it.address
                                tvPhoneNumber.text = it.phoneNumber
                            }

                            fuelAdapter.submitList(state.fuels)
                        }

                        clMain.isVisible = !(state.isLoading || state.hasError)
                        clProgressBarEmpty.isVisible = state.isLoading || state.hasError
                        progressBar.isVisible = state.isLoading
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
                            is FuelStationDetailsViewModel.UiEvent.ShowMessage -> {
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Error")
                                    .setMessage(event.message)
                                    .setPositiveButton("OK") { _, _ -> }
                                    .create()
                                    .show()
                            }
                            is FuelStationDetailsViewModel.UiEvent.ShowMessageAndSignOut -> {
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
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync {
            googleMap = it

            googleMap?.apply {
                isBuildingsEnabled = false
                isIndoorEnabled = false

                uiSettings.apply {
                    isScrollGesturesEnabled = false
                    isZoomControlsEnabled = false
                    isRotateGesturesEnabled = false
                    isTiltGesturesEnabled = false
                    isMapToolbarEnabled = false
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.mapState.collectLatest { state ->
                        if (state.latitude == null) return@collectLatest
                        val latitude = state.latitude
                        val longitude = state.longitude!!

                        it.clear()

                        it.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    latitude,
                                    longitude
                                ),
                                16f
                            )
                        )

                        it.addMarker(
                            MarkerOptions()
                                .position(LatLng(latitude, longitude))
                                .icon(BitmapDescriptorFactory.defaultMarker())
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
package com.dimitriskikidis.owner.fuelapp.presentation.editfuelstation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.dimitriskikidis.owner.fuelapp.R
import com.dimitriskikidis.owner.fuelapp.databinding.FragmentEditFuelStationBinding
import com.dimitriskikidis.owner.fuelapp.presentation.LocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditFuelStationFragment : Fragment() {

    private var _binding: FragmentEditFuelStationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditFuelStationViewModel by viewModels()
    private val locationViewModel: LocationViewModel by hiltNavGraphViewModels(R.id.editFuelStationFragment)

    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditFuelStationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            setupMap()

            mactvBrand.doAfterTextChanged {
                val brand = it.toString()
                viewModel.onBrandChange(brand)
            }

            tietName.doAfterTextChanged {
                val name = it.toString()
                viewModel.onNameChange(name)
            }

            tietCity.doAfterTextChanged {
                val city = it.toString()
                viewModel.onCityChanged(city)
            }

            tietAddress.doAfterTextChanged {
                val address = it.toString()
                viewModel.onAddressChange(address)
            }

            tietPostalCode.doAfterTextChanged {
                val postalCode = it.toString()
                viewModel.onPostalCodeChanged(postalCode)
            }

            tietPhoneNumber.doAfterTextChanged {
                val phoneNumber = it.toString()
                viewModel.onPhoneNumberChange(phoneNumber)
            }

            btnSetLocation.setOnClickListener {
                locationViewModel.latitude = viewModel.latitude
                locationViewModel.longitude = viewModel.longitude

                findNavController().navigate(
                    R.id.action_editFuelStationFragment_to_pickLocationFragment
                )
            }

            btnSave.setOnClickListener {
                viewModel.onSave()
            }

            setFragmentResultListener("PickLocationFragment") { requestKey: String, bundle: Bundle ->
                val latitude = bundle.getDouble("latitude")
                val longitude = bundle.getDouble("longitude")
                tietLatitude.setText(latitude.toString())
                tietLongitude.setText(longitude.toString())
                viewModel.onLocationChange(latitude, longitude)
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collectLatest { state ->
                        if (!state.isLoading && state.error == null) {
                            clMain.isVisible = true
                            clProgressBarError.isVisible = false

                            (tilBrand.editText as? MaterialAutoCompleteTextView)
                                ?.setSimpleItems(viewModel.brandNames)
                            viewModel.selectedBrandName?.let {
                                mactvBrand.setText(it, false)
                            }
                            tilBrand.apply {
                                error = state.brandError
                                isErrorEnabled = state.brandError != null
                            }

                            tietName.setText(viewModel.name)
                            tilName.apply {
                                error = state.nameError
                                isErrorEnabled = state.nameError != null
                            }

                            tietCity.setText(viewModel.city)
                            tilCity.apply {
                                error = state.cityError
                                isErrorEnabled = state.cityError != null
                            }

                            tietAddress.setText(viewModel.address)
                            tilAddress.apply {
                                error = state.addressError
                                isErrorEnabled = state.addressError != null
                            }

                            tietPostalCode.setText(viewModel.postalCode)
                            tilPostalCode.apply {
                                error = state.postalCodeError
                                isErrorEnabled = state.postalCodeError != null
                            }

                            tietPhoneNumber.setText(viewModel.phoneNumber)
                            tilPhoneNumber.apply {
                                error = state.phoneNumberError
                                isErrorEnabled = state.phoneNumberError != null
                            }

                            tvLocationError.apply {
                                text = state.locationError
                                isVisible = state.locationError != null
                            }

                            viewModel.latitude?.let {
                                tietLatitude.setText(it.toString())
                            }
                            viewModel.longitude?.let {
                                tietLongitude.setText(it.toString())
                            }

                            clMain.isVisible = true
                            clProgressBarError.isVisible = false
                        } else {
                            progressBar.isVisible = state.isLoading
//                            tvError.isVisible = state.error != null
//                            state.error?.let { tvError.text = it }
                            clMain.isVisible = false
                            clProgressBarError.isVisible = true
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
                            is EditFuelStationViewModel.UiEvent.ShowMessage -> {
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle(event.title)
                                    .setMessage(event.message)
                                    .setPositiveButton("OK") { _, _ -> }
                                    .create()
                                    .show()
                            }
                            is EditFuelStationViewModel.UiEvent.SetFragmentResult -> {
                                setFragmentResult(
                                    "EditFuelStationFragment",
                                    bundleOf()
                                )
                            }
                            is EditFuelStationViewModel.UiEvent.ShowMessageAndSignOut -> {
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
                    isZoomControlsEnabled = true
                    isRotateGesturesEnabled = false
                    isTiltGesturesEnabled = false
                    isMapToolbarEnabled = false
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.mapState.collectLatest { state ->
                        binding.map.isVisible = state.isVisible
                        if (!state.isVisible) return@collectLatest
                        val latitude = state.latitude!!
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
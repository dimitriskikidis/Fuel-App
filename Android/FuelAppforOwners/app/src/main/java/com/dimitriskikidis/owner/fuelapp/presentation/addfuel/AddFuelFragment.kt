package com.dimitriskikidis.owner.fuelapp.presentation.addfuel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.dimitriskikidis.owner.fuelapp.R
import com.dimitriskikidis.owner.fuelapp.databinding.FragmentAddFuelBinding
import com.dimitriskikidis.owner.fuelapp.presentation.FuelsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddFuelFragment : Fragment() {

    private var _binding: FragmentAddFuelBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddFuelViewModel by viewModels()
    private val fuelsViewModel: FuelsViewModel by hiltNavGraphViewModels(R.id.fuelListFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddFuelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            if (viewModel.fuelTypeIds == null) {
                viewModel.onInitFuelTypeIds(fuelsViewModel.fuelTypeIds)
            }

            mactvBrandFuel.doAfterTextChanged {
                val brandFuel = it.toString()
                viewModel.onBrandFuelChange(brandFuel)
            }

            tietPrice.doAfterTextChanged {
                val price = it.toString()
                viewModel.onPriceChange(price)
            }

            btnAdd.setOnClickListener {
                viewModel.onAdd()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collectLatest { state ->
                        if (!state.isLoading && state.error == null) {
                            (tilBrandFuel.editText as? MaterialAutoCompleteTextView)
                                ?.setSimpleItems(viewModel.brandFuelNames)
                            viewModel.selectedBrandFuelName?.let {
                                mactvBrandFuel.setText(it, false)
                            }
                            tilBrandFuel.apply {
                                error = state.brandFuelError
                                isErrorEnabled = state.brandFuelError != null
                            }

                            tietPrice.setText(viewModel.price)
                            tilPrice.apply {
                                error = state.priceError
                                isErrorEnabled = state.priceError != null
                            }
                        }

                        progressBar.isVisible = state.isLoading
                        groupMain.isVisible = !state.isLoading && state.error == null
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
                            is AddFuelViewModel.UiEvent.NavigateBackWithResult -> {
                                setFragmentResult(
                                    "AddFuelFragment",
                                    bundleOf()
                                )
                                findNavController().popBackStack()
                            }
                            is AddFuelViewModel.UiEvent.ShowMessage -> {
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Error")
                                    .setMessage(event.message)
                                    .setPositiveButton("OK") { _, _ -> }
                                    .create()
                                    .show()
                            }
                            is AddFuelViewModel.UiEvent.ShowMessageAndSignOut -> {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
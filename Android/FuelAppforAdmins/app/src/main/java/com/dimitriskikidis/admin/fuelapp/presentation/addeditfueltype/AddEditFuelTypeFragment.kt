package com.dimitriskikidis.admin.fuelapp.presentation.addeditfueltype

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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
import com.dimitriskikidis.admin.fuelapp.R
import com.dimitriskikidis.admin.fuelapp.databinding.FragmentAddEditFuelTypeBinding
import com.dimitriskikidis.admin.fuelapp.presentation.FuelTypesViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEditFuelTypeFragment : Fragment() {

    private var _binding: FragmentAddEditFuelTypeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditFuelTypeViewModel by viewModels()
    private val fuelTypesViewModel: FuelTypesViewModel by hiltNavGraphViewModels(R.id.fuelTypeListFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditFuelTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            if (viewModel.currentFuelType == null) {
                fuelTypesViewModel.currentFuelType?.let { fuelType ->
                    viewModel.onEvent(AddEditFuelTypeEvent.OnInitFuelType(fuelType))
                }
            }

            viewModel.fuelTypeName?.let {
                tietFuelTypeName.setText(it)
            }

            if (viewModel.currentFuelType == null) {
                (activity as AppCompatActivity).supportActionBar?.title = "Add Fuel Type"
                btnAddSave.text = "Add"
            } else {

                (activity as AppCompatActivity).supportActionBar?.title = "Edit Fuel Type"
                btnAddSave.text = "Save"
            }

            tietFuelTypeName.doAfterTextChanged {
                val name = it.toString()
                viewModel.onEvent(AddEditFuelTypeEvent.OnNameChange(name))
            }

            btnAddSave.setOnClickListener {
                viewModel.onEvent(
                    AddEditFuelTypeEvent.OnAddSave(
                        fuelTypesViewModel.fuelTypeNames
                    )
                )
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collectLatest { state ->
                        tilFuelTypeName.apply {
                            error = state.nameError
                            isErrorEnabled = state.nameError != null
                        }

                        groupMain.isVisible = !state.isLoading
                        progressBar.isVisible = state.isLoading
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
                            is AddEditFuelTypeViewModel.UiEvent.ShowMessageAndNavigateBack -> {
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle(event.title)
                                    .setMessage(event.message)
                                    .setPositiveButton("OK") { _, _ -> }
                                    .setOnDismissListener {
                                        setFragmentResult(
                                            "AddEditFuelTypeFragment",
                                            bundleOf()
                                        )
                                        findNavController().popBackStack()
                                    }
                                    .create()
                                    .show()
                            }
                            is AddEditFuelTypeViewModel.UiEvent.ShowMessageAndSignOut -> {
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
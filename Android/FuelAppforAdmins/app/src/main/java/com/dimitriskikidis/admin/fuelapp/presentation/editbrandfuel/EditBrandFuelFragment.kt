package com.dimitriskikidis.admin.fuelapp.presentation.editbrandfuel

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
import com.dimitriskikidis.admin.fuelapp.R
import com.dimitriskikidis.admin.fuelapp.databinding.FragmentEditBrandFuelBinding
import com.dimitriskikidis.admin.fuelapp.presentation.BrandFuelsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditBrandFuelFragment : Fragment() {

    private var _binding: FragmentEditBrandFuelBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditBrandFuelViewModel by viewModels()
    private val brandFuelsViewModel: BrandFuelsViewModel by hiltNavGraphViewModels(R.id.brandFuelListFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBrandFuelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            if (viewModel.currentBrandFuel == null) {
                viewModel.onInitBrandFuel(brandFuelsViewModel.currentBrandFuel)
            }

            tietName.setText(viewModel.brandFuelName!!)

            smEnabled.isChecked = viewModel.brandFuelIsEnabled!!
            smEnabled.text = if (smEnabled.isChecked) {
                "Enabled"
            } else {
                "Disabled"
            }

            tietName.doAfterTextChanged {
                val name = it.toString()
                viewModel.onNameChange(name)
            }

            smEnabled.setOnCheckedChangeListener { buttonView, isChecked ->
                smEnabled.text = if (smEnabled.isChecked) {
                    "Enabled"
                } else {
                    "Disabled"
                }
                viewModel.onCheckedChange(isChecked)
            }

            btnSave.setOnClickListener {
                viewModel.onSave()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collectLatest { state ->
                        tilName.apply {
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
                            is EditBrandFuelViewModel.UiEvent.ShowMessageAndNavigateBack -> {
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle(event.title)
                                    .setMessage(event.message)
                                    .setPositiveButton("OK") { _, _ -> }
                                    .setOnDismissListener {
                                        setFragmentResult(
                                            "EditBrandFuelFragment",
                                            bundleOf()
                                        )
                                        findNavController().popBackStack()
                                    }
                                    .create()
                                    .show()
                            }
                            is EditBrandFuelViewModel.UiEvent.ShowMessageAndSignOut -> {
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
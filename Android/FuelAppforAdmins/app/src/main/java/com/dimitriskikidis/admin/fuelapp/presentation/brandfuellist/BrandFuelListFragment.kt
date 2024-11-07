package com.dimitriskikidis.admin.fuelapp.presentation.brandfuellist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dimitriskikidis.admin.fuelapp.R
import com.dimitriskikidis.admin.fuelapp.databinding.FragmentBrandFuelListBinding
import com.dimitriskikidis.admin.fuelapp.domain.models.BrandFuel
import com.dimitriskikidis.admin.fuelapp.presentation.BrandFuelsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BrandFuelListFragment : Fragment() {

    private var _binding: FragmentBrandFuelListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BrandFuelListViewModel by viewModels()
    private val brandFuelsViewModel: BrandFuelsViewModel by hiltNavGraphViewModels(R.id.brandFuelListFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBrandFuelListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val brandFuelClickListener = object : BrandFuelAdapter.OnBrandFuelClickListener {
            override fun onBrandFuelEdit(brandFuel: BrandFuel) {
                brandFuelsViewModel.currentBrandFuel = brandFuel
                findNavController().navigate(
                    R.id.action_brandFuelListFragment_to_editBrandFuelFragment
                )
            }
        }

        val brandFuelAdapter = BrandFuelAdapter(brandFuelClickListener)
        binding.apply {
            mactvBrand.doAfterTextChanged {
                val brand = it.toString()
                viewModel.onBrandChange(brand)
            }

            rvBrandFuels.apply {
                adapter = brandFuelAdapter
                layoutManager = LinearLayoutManager(requireContext())
//                setHasFixedSize(true)
            }

            setFragmentResultListener("EditBrandFuelFragment") { requestKey: String, bundle: Bundle ->
                viewModel.onBrandFuelEditComplete()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collectLatest { state ->
                        if (!state.isLoading && state.error == null) {
                            (tilBrand.editText as? MaterialAutoCompleteTextView)
                                ?.setSimpleItems(viewModel.brandNames)
                            viewModel.selectedBrandName?.let { mactvBrand.setText(it, false) }
                            clMain.isVisible = true
                            clProgressBarError.isVisible = false
                            brandFuelAdapter.submitList(state.brandFuels) {
                                rvBrandFuels.scrollToPosition(0)
                            }
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
                            is BrandFuelListViewModel.UiEvent.ShowMessage -> {
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle(event.title)
                                    .setMessage(event.message)
                                    .setPositiveButton("OK") { _, _ -> }
                                    .create()
                                    .show()
                            }
                            is BrandFuelListViewModel.UiEvent.ShowMessageAndSignOut -> {
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
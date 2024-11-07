package com.dimitriskikidis.admin.fuelapp.presentation.fueltypelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
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
import com.dimitriskikidis.admin.fuelapp.databinding.FragmentFuelTypeListBinding
import com.dimitriskikidis.admin.fuelapp.domain.models.FuelType
import com.dimitriskikidis.admin.fuelapp.presentation.FuelTypesViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FuelTypeListFragment : Fragment() {

    private var _binding: FragmentFuelTypeListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FuelTypeListViewModel by viewModels()
    private val fuelTypesViewModel: FuelTypesViewModel by hiltNavGraphViewModels(R.id.fuelTypeListFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFuelTypeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fuelTypeClickListener = object : FuelTypeAdapter.OnFuelTypeClickListener {
            override fun onFuelTypeEdit(fuelType: FuelType) {
                fuelTypesViewModel.currentFuelType = fuelType
                findNavController().navigate(
                    R.id.action_fuelTypeListFragment_to_addEditFuelTypeFragment
                )
            }

            override fun onFuelTypeDelete(fuelType: FuelType) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete fuel type?")
                    .setMessage(
                        "Are you sure you want to delete the fuel type '${fuelType.name}'?"
                    )
                    .setPositiveButton("DELETE") { _, _ ->
                        viewModel.onEvent(FuelTypeListEvent.OnFuelTypeDeleteConfirm(fuelType))
                    }
                    .setNegativeButton("CANCEL") { _, _ -> }
                    .create()
                    .show()
            }
        }

        val fuelTypeListAdapter = FuelTypeAdapter(fuelTypeClickListener)

        binding.apply {
            btnAddFuelType.setOnClickListener {
                fuelTypesViewModel.currentFuelType = null
                findNavController().navigate(
                    R.id.action_fuelTypeListFragment_to_addEditFuelTypeFragment
                )
            }

            rvFuelTypes.apply {
                adapter = fuelTypeListAdapter
                layoutManager = LinearLayoutManager(requireContext())
//                setHasFixedSize(true)
            }

            setFragmentResultListener("AddEditFuelTypeFragment") { requestKey: String, bundle: Bundle ->
                viewModel.onEvent(FuelTypeListEvent.OnFuelTypeAddEditComplete)
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collectLatest { state ->
                        if (!state.isLoading && state.error == null) {
                            fuelTypesViewModel.mapFuelTypeNames(state.fuelTypes)
                            clMain.isVisible = true
                            clProgressBarError.isVisible = false
                            fuelTypeListAdapter.submitList(state.fuelTypes) {
                                rvFuelTypes.scrollToPosition(0)
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
                            is FuelTypeListViewModel.UiEvent.ShowMessage -> {
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle(event.title)
                                    .setMessage(event.message)
                                    .setPositiveButton("OK") { _, _ -> }
                                    .create()
                                    .show()
                            }
                            is FuelTypeListViewModel.UiEvent.ShowMessageAndSignOut -> {
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
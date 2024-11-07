package com.dimitriskikidis.owner.fuelapp.presentation.fuellist

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
import com.dimitriskikidis.owner.fuelapp.R
import com.dimitriskikidis.owner.fuelapp.databinding.FragmentFuelListBinding
import com.dimitriskikidis.owner.fuelapp.domain.models.Fuel
import com.dimitriskikidis.owner.fuelapp.presentation.FuelsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FuelListFragment : Fragment() {

    private var _binding: FragmentFuelListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FuelListViewModel by viewModels()
    private val fuelsViewModel: FuelsViewModel by hiltNavGraphViewModels(R.id.fuelListFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFuelListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fuelClickListener = object : FuelAdapter.OnFuelClickListener {
            override fun onEditFuel(fuel: Fuel) {
                fuelsViewModel.currentFuel = fuel
                findNavController().navigate(
                    R.id.action_fuelListFragment_to_editFuelFragment
                )
            }

            override fun onDeleteFuel(fuel: Fuel) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete fuel?")
                    .setMessage(
                        "Are you sure you want to delete the fuel '${fuel.name}'?"
                    )
                    .setPositiveButton("DELETE") { _, _ ->
                        viewModel.onDeleteFuelConfirm(fuel)
                    }
                    .setNegativeButton("CANCEL") { _, _ -> }
                    .create()
                    .show()
            }
        }

        val fuelAdapter = FuelAdapter(fuelClickListener)

        binding.apply {
            btnAddFuel.setOnClickListener {
                findNavController().navigate(
                    R.id.action_fuelListFragment_to_addFuelFragment
                )
            }

            rvFuels.apply {
                adapter = fuelAdapter
                layoutManager = LinearLayoutManager(requireContext())
//                setHasFixedSize(true)
            }

            setFragmentResultListener("AddFuelFragment") { requestKey: String, bundle: Bundle ->
                viewModel.onAddEditFuelComplete()
            }

            setFragmentResultListener("EditFuelFragment") { requestKey: String, bundle: Bundle ->
                viewModel.onAddEditFuelComplete()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collectLatest { state ->
                        if (!state.isLoading && state.error == null) {
                            fuelsViewModel.mapFuelTypeIds(state.fuels)
                            clMain.isVisible = true
                            clProgressBarError.isVisible = false
                            fuelAdapter.submitList(state.fuels) {
                                rvFuels.scrollToPosition(0)
                            }
                            tvNoFuels.isVisible = state.fuels.isEmpty()
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
                            is FuelListViewModel.UiEvent.ShowMessage -> {
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Error")
                                    .setMessage(event.message)
                                    .setPositiveButton("OK") { _, _ -> }
                                    .create()
                                    .show()
                            }
                            is FuelListViewModel.UiEvent.ShowMessageAndSignOut -> {
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
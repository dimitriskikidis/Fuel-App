package com.dimitriskikidis.admin.fuelapp.presentation.brandlist

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
import com.dimitriskikidis.admin.fuelapp.databinding.FragmentBrandListBinding
import com.dimitriskikidis.admin.fuelapp.domain.models.Brand
import com.dimitriskikidis.admin.fuelapp.presentation.BrandsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BrandListFragment : Fragment() {

    private var _binding: FragmentBrandListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BrandListViewModel by viewModels()
    private val brandsViewModel: BrandsViewModel by hiltNavGraphViewModels(R.id.brandListFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBrandListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val brandClickListener = object : BrandAdapter.OnBrandClickListener {
            override fun onBrandEdit(brand: Brand) {
                brandsViewModel.currentBrand = brand
                findNavController().navigate(
                    R.id.action_brandListFragment_to_addEditBrandFragment
                )
            }

            override fun onBrandDelete(brand: Brand) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete brand?")
                    .setMessage(
                        "Are you sure you want to delete the brand '${brand.name}'?"
                    )
                    .setPositiveButton("DELETE") { _, _ ->
                        viewModel.onEvent(BrandListEvent.OnBrandDeleteConfirm(brand))
                    }
                    .setNegativeButton("CANCEL") { _, _ -> }
                    .create()
                    .show()
            }
        }

        val brandAdapter = BrandAdapter(brandClickListener)
        binding.apply {
            btnAddBrand.setOnClickListener {
                brandsViewModel.currentBrand = null
                findNavController().navigate(
                    R.id.action_brandListFragment_to_addEditBrandFragment
                )
            }

            rvBrands.apply {
                adapter = brandAdapter
                layoutManager = LinearLayoutManager(requireContext())
//                setHasFixedSize(true)
            }

            setFragmentResultListener("AddEditBrandFragment") { requestKey: String, bundle: Bundle ->
                viewModel.onEvent(BrandListEvent.OnBrandAddEditComplete)
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collectLatest { state ->
                        if (!state.isLoading && state.error == null) {
                            brandsViewModel.mapBrandNames(state.brands)
                            clMain.isVisible = true
                            clProgressBarError.isVisible = false
                            brandAdapter.submitList(state.brands) {
                                rvBrands.scrollToPosition(0)
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
                            is BrandListViewModel.UiEvent.ShowMessage -> {
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle(event.title)
                                    .setMessage(event.message)
                                    .setPositiveButton("OK") { _, _ -> }
                                    .create()
                                    .show()
                            }
                            is BrandListViewModel.UiEvent.ShowMessageAndSignOut -> {
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
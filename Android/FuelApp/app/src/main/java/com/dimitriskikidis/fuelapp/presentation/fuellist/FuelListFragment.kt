package com.dimitriskikidis.fuelapp.presentation.fuellist

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dimitriskikidis.fuelapp.R
import com.dimitriskikidis.fuelapp.databinding.FragmentFuelListBinding
import com.dimitriskikidis.fuelapp.domain.models.FuelStation
import com.dimitriskikidis.fuelapp.presentation.DataViewModel
import com.dimitriskikidis.fuelapp.presentation.FuelStationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FuelListFragment : Fragment(), MenuProvider {

    private var _binding: FragmentFuelListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FuelListViewModel by viewModels()
    private val dataViewModel: DataViewModel by hiltNavGraphViewModels(R.id.main_nav_graph)
    private val fuelStationViewModel: FuelStationViewModel by hiltNavGraphViewModels(R.id.main_nav_graph)

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

        viewModel.onInitDataState(dataViewModel.state)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.CREATED)

        val fuelClickListener = object : FuelSearchResultAdapter.OnFuelClickListener {
            override fun onFuelClick(fuelStation: FuelStation) {
                fuelStationViewModel.fuelStationId = fuelStation.id
                findNavController().navigate(
                    R.id.action_fuelListFragment_to_fuelStationDetailsFragment
                )
            }
        }

        val fuelSearchResultAdapter = FuelSearchResultAdapter(fuelClickListener)

        binding.apply {
            rvFuelList.apply {
                adapter = fuelSearchResultAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collectLatest { state ->
                        fuelSearchResultAdapter.submitList(state.sortedFuelSearchResults) {
                            rvFuelList.scrollToPosition(0)
                        }
                        tvEmptyList.isVisible = state.sortedFuelSearchResults.isEmpty()
                    }
                }
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.fuel_list_fragment_menu, menu)
        if (viewModel.location == null) {
            menu.findItem(R.id.action_sort_by_price).isChecked = true
            menu.findItem(R.id.action_sort_by_distance).isEnabled = false
        } else {
            when (viewModel.getSortOrder()) {
                "sortByPrice" -> menu.findItem(R.id.action_sort_by_price).isChecked = true
                "sortByDistance" -> menu.findItem(R.id.action_sort_by_distance).isChecked = true
            }
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_sort_by_price -> {
                viewModel.onSortOrderChange("sortByPrice")
            }
            R.id.action_sort_by_distance -> {
                viewModel.onSortOrderChange("sortByDistance")
            }
        }

        menuItem.isChecked = true
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
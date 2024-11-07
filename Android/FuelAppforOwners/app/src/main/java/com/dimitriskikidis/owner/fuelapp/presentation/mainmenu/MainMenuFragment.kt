package com.dimitriskikidis.owner.fuelapp.presentation.mainmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.dimitriskikidis.owner.fuelapp.R
import com.dimitriskikidis.owner.fuelapp.databinding.FragmentMainMenuBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainMenuFragment : Fragment() {

    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainMenuViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnAccount.setOnClickListener {
                findNavController().navigate(
                    R.id.action_mainMenuFragment_to_accountFragment
                )
            }

            btnEditFuelStation.setOnClickListener {
                findNavController().navigate(
                    R.id.action_mainMenuFragment_to_editFuelStationFragment
                )
            }

            btnFuels.setOnClickListener {
                findNavController().navigate(
                    R.id.action_mainMenuFragment_to_fuelListFragment
                )
            }

            btnReviews.setOnClickListener {
                findNavController().navigate(
                    R.id.action_mainMenuFragment_to_reviewListFragment
                )
            }

            setFragmentResultListener("EditFuelStationFragment") { requestKey: String, bundle: Bundle ->
                viewModel.onCreateFuelStation()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collectLatest { state ->
                        btnFuels.isVisible = state.hasFuelStation
                        btnReviews.isVisible = state.hasFuelStation
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
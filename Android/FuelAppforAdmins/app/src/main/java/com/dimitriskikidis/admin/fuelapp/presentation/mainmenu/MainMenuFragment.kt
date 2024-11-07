package com.dimitriskikidis.admin.fuelapp.presentation.mainmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dimitriskikidis.admin.fuelapp.R
import com.dimitriskikidis.admin.fuelapp.databinding.FragmentMainMenuBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainMenuFragment : Fragment() {

    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

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

            btnBrands.setOnClickListener {
                findNavController().navigate(
                    R.id.action_mainMenuFragment_to_brandListFragment
                )
            }

            btnFuelTypes.setOnClickListener {
                findNavController().navigate(
                    R.id.action_mainMenuFragment_to_fuelTypeListFragment
                )
            }

            btnBrandFuels.setOnClickListener {
                findNavController().navigate(
                    R.id.action_mainMenuFragment_to_brandFuelListFragment
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
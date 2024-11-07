package com.dimitriskikidis.fuelapp.presentation.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dimitriskikidis.fuelapp.R
import com.dimitriskikidis.fuelapp.databinding.FragmentAccountBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvUsername.text = viewModel.username
            tvEmail.text = viewModel.email

            btnReviews.setOnClickListener {
                findNavController().navigate(
                    R.id.action_accountFragment_to_userReviewListFragment
                )
            }

            btnSignOut.setOnClickListener {
//                MaterialAlertDialogBuilder(requireContext())
//                    .setTitle("Sign out?")
//                    .setMessage(
//                        "Are you sure you want to sign out?"
//                    )
//                    .setPositiveButton("SIGN OUT") { _, _ ->
//                        viewModel.onSignOut()
//                        findNavController().navigate(
//                            R.id.action_global_signInFragment
//                        )
//                    }
//                    .setNegativeButton("CANCEL") { _, _ -> }
//                    .create()
//                    .show()
                viewModel.onSignOut()
                findNavController().navigate(
                    R.id.action_global_signInFragment
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
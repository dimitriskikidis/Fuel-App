package com.dimitriskikidis.admin.fuelapp.presentation.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dimitriskikidis.admin.fuelapp.R
import com.dimitriskikidis.admin.fuelapp.databinding.FragmentSignInBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tietEmail.setText(viewModel.email)
            tietPassword.setText(viewModel.password)

            tietEmail.doAfterTextChanged {
                val email = it.toString()
                viewModel.onEvent(SignInEvent.OnEmailChange(email))
            }

            tietPassword.doAfterTextChanged {
                val password = it.toString()
                viewModel.onEvent(SignInEvent.OnPasswordChange(password))
            }

            btnSignIn.setOnClickListener {
                viewModel.onEvent(SignInEvent.OnSignIn)
            }

            btnSignUp.setOnClickListener {
                findNavController().navigate(
                    R.id.action_signInFragment_to_signUpFragment
                )
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collectLatest { state ->
                        tilEmail.apply {
                            error = state.error
                            isErrorEnabled = state.error != null
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
                            is SignInViewModel.UiEvent.NavigateToMainMenu -> {
                                val navOptions = NavOptions.Builder()
                                    .setPopUpTo(R.id.nav_graph, false)
                                    .build()

                                findNavController().navigate(
                                    R.id.action_signInFragment_to_mainMenuFragment,
                                    null,
                                    navOptions
                                )
                            }
                            is SignInViewModel.UiEvent.ShowMessage -> {
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Error")
                                    .setMessage(event.message)
                                    .setPositiveButton("OK") { _, _ -> }
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
package com.dimitriskikidis.admin.fuelapp.presentation.signup

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
import com.dimitriskikidis.admin.fuelapp.databinding.FragmentSignUpBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tietEmail.setText(viewModel.email)
            tietPassword.setText(viewModel.password)
            tietConfirmPassword.setText(viewModel.confirmPassword)

            tietEmail.doAfterTextChanged {
                val email = it.toString()
                viewModel.onEvent(SignUpEvent.OnEmailChange(email))
            }

            tietPassword.doAfterTextChanged {
                val password = it.toString()
                viewModel.onEvent(SignUpEvent.OnPasswordChange(password))
            }

            tietConfirmPassword.doAfterTextChanged {
                val confirmPassword = it.toString()
                viewModel.onEvent(SignUpEvent.OnConfirmPasswordChange(confirmPassword))
            }

            btnSignUp.setOnClickListener {
                viewModel.onEvent(SignUpEvent.OnSignUp)
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collectLatest { state ->
                        tilEmail.apply {
                            error = state.emailError
                            isErrorEnabled = state.emailError != null
                        }

                        tilPassword.apply {
                            error = state.passwordError
                            isErrorEnabled = state.passwordError != null
                        }

                        tilConfirmPassword.apply {
                            error = state.confirmPasswordError
                            isErrorEnabled = state.confirmPasswordError != null
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
                            is SignUpViewModel.UiEvent.NavigateToMainMenu -> {
                                val navOptions = NavOptions.Builder()
                                    .setPopUpTo(R.id.nav_graph, false)
                                    .build()

                                findNavController().navigate(
                                    R.id.action_signUpFragment_to_mainMenuFragment,
                                    null,
                                    navOptions
                                )
                            }
                            is SignUpViewModel.UiEvent.ShowMessage -> {
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
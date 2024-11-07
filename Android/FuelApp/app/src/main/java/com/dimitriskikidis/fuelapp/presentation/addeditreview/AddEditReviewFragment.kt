package com.dimitriskikidis.fuelapp.presentation.addeditreview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.dimitriskikidis.fuelapp.R
import com.dimitriskikidis.fuelapp.databinding.FragmentAddEditReviewBinding
import com.dimitriskikidis.fuelapp.presentation.FuelStationViewModel
import com.dimitriskikidis.fuelapp.presentation.ReviewDataViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEditReviewFragment : Fragment() {

    private var _binding: FragmentAddEditReviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditReviewViewModel by viewModels()
    private val fuelStationViewModel: FuelStationViewModel by hiltNavGraphViewModels(R.id.main_nav_graph)
    private val reviewDataViewModel: ReviewDataViewModel by hiltNavGraphViewModels(R.id.main_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            if (viewModel.fuelStationId == null) {
                val fuelStationId = fuelStationViewModel.fuelStationId!!
                val review = reviewDataViewModel.currentReviewData
                viewModel.onInitData(fuelStationId, review)
            }

            rbRating.rating = viewModel.rating
            tietReview.setText(viewModel.text)

            if (viewModel.currentReviewData == null) {
                (activity as AppCompatActivity).supportActionBar?.title = "Add Review"
            } else {
                (activity as AppCompatActivity).supportActionBar?.title = "Edit Review"
            }

            rbRating.setOnRatingBarChangeListener { _, rating, fromUser ->
                if (fromUser) {
                    viewModel.onRatingChanged(rating)
                }
            }

            tietReview.doAfterTextChanged {
                val text = it.toString()
                viewModel.onTextChanged(text)
            }

            btnPublish.setOnClickListener {
                viewModel.onSubmit()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.canPublish.collectLatest { canPublish ->
                        btnPublish.isEnabled = canPublish
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collectLatest { state ->
                        clMain.isVisible = !state.isLoading
                        progressBar.isVisible = state.isLoading
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
                            is AddEditReviewViewModel.UiEvent.NavigateBackWithResult -> {
                                setFragmentResult(
                                    "UserReviewUpdate@AERF",
                                    bundleOf()
                                )
                                findNavController().popBackStack()
                            }
                            is AddEditReviewViewModel.UiEvent.ShowMessage -> {
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Error")
                                    .setMessage(event.message)
                                    .setPositiveButton("OK") { _, _ -> }
                                    .create()
                                    .show()
                            }
                            is AddEditReviewViewModel.UiEvent.ShowMessageAndSignOut -> {
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
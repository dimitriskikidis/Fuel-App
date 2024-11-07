package com.dimitriskikidis.fuelapp.presentation.reviewlist

import android.icu.text.DecimalFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dimitriskikidis.fuelapp.R
import com.dimitriskikidis.fuelapp.data.mappers.toReviewData
import com.dimitriskikidis.fuelapp.databinding.FragmentReviewListBinding
import com.dimitriskikidis.fuelapp.presentation.FuelStationViewModel
import com.dimitriskikidis.fuelapp.presentation.ReviewDataViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class ReviewListFragment : Fragment() {

    private var _binding: FragmentReviewListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReviewListViewModel by viewModels()
    private val fuelStationViewModel: FuelStationViewModel by hiltNavGraphViewModels(R.id.main_nav_graph)
    private val reviewDataViewModel: ReviewDataViewModel by hiltNavGraphViewModels(R.id.main_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.fuelStationId == null) {
            viewModel.onInitFuelStationId(fuelStationViewModel.fuelStationId!!)
        }

        val reviewAdapter = ReviewAdapter()
        val ratingFormatter = DecimalFormat("0.0").apply {
            roundingMode = RoundingMode.HALF_UP.ordinal
        }
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter
            .ofPattern("dd/MM/yyyy")

        binding.apply {
            btnAddReview.setOnClickListener {
                reviewDataViewModel.currentReviewData = null
                findNavController().navigate(
                    R.id.action_reviewListFragment_to_addEditReviewFragment
                )
            }

            btnEditReview.setOnClickListener {
                reviewDataViewModel.currentReviewData = viewModel.userReview?.toReviewData()
                findNavController().navigate(
                    R.id.action_reviewListFragment_to_addEditReviewFragment
                )
            }

            btnDeleteReview.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete review?")
                    .setMessage(
                        "Are you sure you want to delete your review?"
                    )
                    .setPositiveButton("DELETE") { _, _ ->
                        viewModel.onDeleteReview()
                    }
                    .setNegativeButton("CANCEL") { _, _ -> }
                    .create()
                    .show()
            }

            rvReviews.apply {
                adapter = reviewAdapter
                layoutManager = LinearLayoutManager(requireContext())
//                setHasFixedSize(true)
            }

            setFragmentResultListener("UserReviewUpdate@AERF") { requestKey: String, bundle: Bundle ->
                setFragmentResult("UserReviewUpdate@RLF", bundleOf())
                viewModel.onAddEditReviewComplete()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collectLatest { state ->
                        clReviewInfo.isVisible = !state.isLoading && state.reviewCount != 0
                        if (!state.isLoading && state.reviewCount != 0) {
                            tvRating.text = ratingFormatter.format(state.rating)
                            rbRating.rating = state.rating
                            tvReviewCount.text = "(${state.reviewCount})"

                            reviewCounts.apply {
                                pbCountPercentage5.progress = state.percentages[0]
                                tvCount5.text = state.counts[0].toString()

                                pbCountPercentage4.progress = state.percentages[1]
                                tvCount4.text = state.counts[1].toString()

                                pbCountPercentage3.progress = state.percentages[2]
                                tvCount3.text = state.counts[2].toString()

                                pbCountPercentage2.progress = state.percentages[3]
                                tvCount2.text = state.counts[3].toString()

                                pbCountPercentage1.progress = state.percentages[4]
                                tvCount1.text = state.counts[4].toString()
                            }
                        }

                        mcvUserReview.isVisible = viewModel.userReview != null
                        btnAddReview.isVisible = viewModel.userReview == null
                        viewModel.userReview?.let { userReview ->
                            clReview.apply {
                                rbRating.rating = userReview.rating.toFloat()
                                tvUsername.text = userReview.username
                                tvLastUpdate.text = dateTimeFormatter.format(userReview.lastUpdate)
                                tvText.text = userReview.text
                                tvText.isVisible = userReview.text.isNotEmpty()
                            }
                        }

                        if (!state.isLoading) {
                            reviewAdapter.submitList(state.reviews)
                        }

                        clUserReview.isVisible = !state.isLoading && state.error == null
                        rvReviews.isVisible = !state.isLoading && state.error == null
                        tvNoReviews.isVisible = !state.isLoading && state.reviews.isEmpty()
                                && state.error == null
                        progressBar.isVisible = state.isLoading
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
                            is ReviewListViewModel.UiEvent.SetFragmentResult -> {
                                setFragmentResult("UserReviewUpdate@RLF", bundleOf())
                            }
                            is ReviewListViewModel.UiEvent.ShowMessage -> {
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Error")
                                    .setMessage(event.message)
                                    .setPositiveButton("OK") { _, _ -> }
                                    .create()
                                    .show()
                            }
                            is ReviewListViewModel.UiEvent.ShowMessageAndSignOut -> {
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
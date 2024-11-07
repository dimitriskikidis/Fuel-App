package com.dimitriskikidis.owner.fuelapp.presentation.reviewlist

import android.icu.text.DecimalFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dimitriskikidis.owner.fuelapp.R
import com.dimitriskikidis.owner.fuelapp.databinding.FragmentReviewListBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.RoundingMode

@AndroidEntryPoint
class ReviewListFragment : Fragment() {

    private var _binding: FragmentReviewListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReviewListViewModel by viewModels()

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

        val reviewAdapter = ReviewAdapter()
        val decimalFormat = DecimalFormat("#.0").apply {
            roundingMode = RoundingMode.HALF_UP.ordinal
        }

        binding.apply {
            rvReviews.apply {
                adapter = reviewAdapter
                layoutManager = LinearLayoutManager(requireContext())
//                setHasFixedSize(true)
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collectLatest { state ->
                        clReviewInfo.isVisible = !state.isLoading && state.reviewCount != 0
                        if (!state.isLoading && state.error == null) {
                            if (state.reviewCount != 0) {
                                clReviewInfo.isVisible = true
                                tvNoReviews.isVisible = false

                                tvRating.text = decimalFormat.format(state.rating)
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

                                reviewAdapter.submitList(state.reviews)
                            } else {
                                clReviewInfo.isVisible = false
                                tvNoReviews.isVisible = true
                            }
                        }

                        progressBar.isVisible = state.isLoading
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
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
package com.dimitriskikidis.fuelapp.presentation.userreviewlist

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
import com.dimitriskikidis.fuelapp.R
import com.dimitriskikidis.fuelapp.data.mappers.toReviewData
import com.dimitriskikidis.fuelapp.databinding.FragmentUserReviewListBinding
import com.dimitriskikidis.fuelapp.domain.models.UserReview
import com.dimitriskikidis.fuelapp.presentation.FuelStationViewModel
import com.dimitriskikidis.fuelapp.presentation.ReviewDataViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserReviewListFragment : Fragment() {

    private var _binding: FragmentUserReviewListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserReviewListViewModel by viewModels()
    private val fuelStationViewModel: FuelStationViewModel by hiltNavGraphViewModels(R.id.main_nav_graph)
    private val reviewDataViewModel: ReviewDataViewModel by hiltNavGraphViewModels(R.id.main_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserReviewListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            val userReviewClickListener = object : UserReviewAdapter.OnUserReviewClickListener {
                override fun onUserReviewClick(userReview: UserReview) {
                    fuelStationViewModel.fuelStationId = userReview.fuelStation.id
                    findNavController().navigate(
                        R.id.action_userReviewListFragment_to_fuelStationDetailsFragment
                    )
                }

                override fun onUserReviewEdit(userReview: UserReview) {
                    fuelStationViewModel.fuelStationId = userReview.fuelStation.id
                    reviewDataViewModel.currentReviewData = userReview.toReviewData()
                    findNavController().navigate(
                        R.id.action_userReviewListFragment_to_addEditReviewFragment
                    )
                }

                override fun onUserReviewDelete(userReview: UserReview) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Delete review?")
                        .setMessage(
                            "Are you sure you want to delete your review?"
                        )
                        .setPositiveButton("DELETE") { _, _ ->
                            viewModel.onDeleteUserReview(userReview)
                        }
                        .setNegativeButton("CANCEL") { _, _ -> }
                        .create()
                        .show()
                }
            }

            val userReviewAdapter = UserReviewAdapter(userReviewClickListener)

            rvReviews.apply {
                adapter = userReviewAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            setFragmentResultListener("UserReviewUpdate@AERF") { requestKey: String, bundle: Bundle ->
                viewModel.onUserReviewUpdate()
            }

            setFragmentResultListener("UserReviewUpdate@FSDF") { requestKey: String, bundle: Bundle ->
                viewModel.onUserReviewUpdate()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collectLatest { state ->
                        if (!state.isLoading) {
                            userReviewAdapter.submitList(state.userReviews) {
                                rvReviews.scrollToPosition(0)
                            }

                        }

                        rvReviews.isVisible = !state.isLoading
                        tvNoReviews.isVisible = !state.isLoading && state.userReviews.isEmpty()
                        progressBar.isVisible = state.isLoading
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
                            is UserReviewListViewModel.UiEvent.ShowMessage -> {
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Error")
                                    .setMessage(event.message)
                                    .setPositiveButton("OK") { _, _ -> }
                                    .create()
                                    .show()
                            }
                            is UserReviewListViewModel.UiEvent.ShowMessageAndSignOut -> {
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
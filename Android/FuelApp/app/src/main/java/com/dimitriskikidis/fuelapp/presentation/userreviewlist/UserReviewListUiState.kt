package com.dimitriskikidis.fuelapp.presentation.userreviewlist

import com.dimitriskikidis.fuelapp.domain.models.UserReview

data class UserReviewListUiState(
    val isLoading: Boolean = false,
    val userReviews: List<UserReview> = emptyList()
)

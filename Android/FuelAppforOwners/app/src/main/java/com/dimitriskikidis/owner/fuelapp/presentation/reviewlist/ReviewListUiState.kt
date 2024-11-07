package com.dimitriskikidis.owner.fuelapp.presentation.reviewlist

import com.dimitriskikidis.owner.fuelapp.domain.models.Review

data class ReviewListUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val percentages: List<Int> = emptyList(),
    val counts: List<Int> = emptyList(),
    val reviews: List<Review> = emptyList()
)

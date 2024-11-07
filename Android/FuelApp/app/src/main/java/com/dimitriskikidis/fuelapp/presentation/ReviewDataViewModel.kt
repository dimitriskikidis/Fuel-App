package com.dimitriskikidis.fuelapp.presentation

import androidx.lifecycle.ViewModel
import com.dimitriskikidis.fuelapp.domain.models.ReviewData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReviewDataViewModel @Inject constructor() : ViewModel() {

    var currentReviewData: ReviewData? = null
}
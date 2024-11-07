package com.dimitriskikidis.fuelapp.data.mappers

import com.dimitriskikidis.fuelapp.domain.models.Review
import com.dimitriskikidis.fuelapp.domain.models.ReviewData
import com.dimitriskikidis.fuelapp.domain.models.UserReview

fun Review.toReviewData(): ReviewData {
    return ReviewData(
        id = id,
        rating = rating,
        text = text
    )
}

fun UserReview.toReviewData(): ReviewData {
    return ReviewData(
        id = id,
        rating = rating,
        text = text
    )
}
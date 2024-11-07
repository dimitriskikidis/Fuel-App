package com.dimitriskikidis.fuelapp.data.mappers

import com.dimitriskikidis.fuelapp.data.remote.dtos.ReviewDto
import com.dimitriskikidis.fuelapp.domain.models.Review
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun ReviewDto.toReview(): Review {
    return Review(
        id = id,
        consumerId = consumerId,
        username = username!!,
        rating = rating,
        text = text,
        lastUpdate = LocalDateTime.parse(lastUpdate)
            .atZone(ZoneOffset.UTC)
            .withZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime()
    )
}
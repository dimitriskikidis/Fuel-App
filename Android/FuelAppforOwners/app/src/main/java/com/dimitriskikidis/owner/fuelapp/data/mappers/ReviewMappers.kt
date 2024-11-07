package com.dimitriskikidis.owner.fuelapp.data.mappers

import com.dimitriskikidis.owner.fuelapp.data.remote.dtos.ReviewDto
import com.dimitriskikidis.owner.fuelapp.domain.models.Review
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun ReviewDto.toReview(): Review {
    return Review(
        id = id,
        username = username,
        rating = rating,
        text = text,
        lastUpdate = LocalDateTime.parse(lastUpdate)
            .atZone(ZoneOffset.UTC)
            .withZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime()
    )
}
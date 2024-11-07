package com.dimitriskikidis.fuelapp.data.mappers

import com.dimitriskikidis.fuelapp.data.remote.dtos.ReviewDto
import com.dimitriskikidis.fuelapp.domain.models.UserReview
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun ReviewDto.toUserReview(): UserReview {
    return UserReview(
        id = id,
        fuelStation = fuelStationDto!!.toFuelStation(),
        rating = rating,
        text = text,
        lastUpdate = LocalDateTime.parse(lastUpdate)
            .atZone(ZoneOffset.UTC)
            .withZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime()
    )
}
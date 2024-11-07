package com.dimitriskikidis.fuelappserver.review;

public record ReviewCreateUpdateRequest(
        Integer rating,
        String text
) {
}

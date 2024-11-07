package com.dimitriskikidis.fuelappserver.consumer;

public record ConsumerSignUpResponse(
        String accessToken,
        Integer consumerId
) {
}

package com.dimitriskikidis.fuelappserver.consumer;

public record ConsumerSignInResponse(
        String accessToken,
        Integer consumerId,
        String username
) {
}

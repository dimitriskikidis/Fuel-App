package com.dimitriskikidis.fuelappserver.consumer;

public record ConsumerSignUpRequest(
        String email,
        String password,
        String username
) {
}

package com.dimitriskikidis.fuelappserver.owner;

public record OwnerSignUpRequest(
        String email,
        String password,
        String firstName,
        String lastName
) {
}

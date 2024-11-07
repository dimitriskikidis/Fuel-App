package com.dimitriskikidis.fuelappserver.admin;

public record AdminSignUpRequest(
        String email,
        String password
) {
}

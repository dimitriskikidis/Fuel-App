package com.dimitriskikidis.fuelappserver.auth;

public record AuthenticationRequest(
        String email,
        String password
) {
}

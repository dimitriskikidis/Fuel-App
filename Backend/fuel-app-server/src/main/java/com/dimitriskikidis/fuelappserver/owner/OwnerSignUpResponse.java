package com.dimitriskikidis.fuelappserver.owner;

public record OwnerSignUpResponse(
        String accessToken,
        Integer ownerId
) {
}

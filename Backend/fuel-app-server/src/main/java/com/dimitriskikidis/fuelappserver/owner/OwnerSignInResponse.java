package com.dimitriskikidis.fuelappserver.owner;

public record OwnerSignInResponse(
        String accessToken,
        Integer ownerId,
        String firstName,
        String lastName,
        Integer fuelStationId
) {
}

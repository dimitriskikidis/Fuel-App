package com.dimitriskikidis.fuelappserver.fuelstation;

public record FuelStationCreateUpdateRequest(
        Integer brandId,

        Double latitude,

        Double longitude,

        String name,
        String city,

        String address,
        String postalCode,

        String phoneNumber
) {
}

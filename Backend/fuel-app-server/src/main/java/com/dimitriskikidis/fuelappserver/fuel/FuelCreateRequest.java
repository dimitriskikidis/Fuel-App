package com.dimitriskikidis.fuelappserver.fuel;

public record FuelCreateRequest(
        Integer brandFuelId,
        Integer price
) {
}

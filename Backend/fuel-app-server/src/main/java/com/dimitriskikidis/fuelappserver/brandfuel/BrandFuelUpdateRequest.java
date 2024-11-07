package com.dimitriskikidis.fuelappserver.brandfuel;

public record BrandFuelUpdateRequest(
        String name,
        Boolean isEnabled
) {
}

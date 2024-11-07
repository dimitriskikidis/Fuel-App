package com.dimitriskikidis.fuelappserver.brandfuel;

public record BrandFuelCreateRequest(
        Integer brandId,
        Integer fuelTypeId,
        String name
) {
}

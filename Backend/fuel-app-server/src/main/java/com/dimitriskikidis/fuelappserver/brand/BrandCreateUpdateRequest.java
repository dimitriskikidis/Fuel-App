package com.dimitriskikidis.fuelappserver.brand;

public record BrandCreateUpdateRequest(
        String name,
        byte[] iconBytes
) {
}

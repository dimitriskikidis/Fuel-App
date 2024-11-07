package com.dimitriskikidis.fuelappserver.fuel;

import java.util.List;

public record FuelSearchRequest(
        List<Integer> brandIds,
        Integer fuelTypeId,
        Double minLatitude,
        Double maxLatitude,
        Double minLongitude,
        Double maxLongitude
) {
}

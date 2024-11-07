package com.dimitriskikidis.fuelappserver.fuel;

import com.dimitriskikidis.fuelappserver.fuelstation.FuelStation;

public record FuelSearchResult(
        Fuel fuel,
        FuelStation fuelStation
) {
}

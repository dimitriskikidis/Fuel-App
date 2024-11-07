package com.dimitriskikidis.fuelappserver.fuel;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/fuels")
@RequiredArgsConstructor
public class FuelController {

    private final FuelService fuelService;

    @PostMapping(path = "search")
    public ResponseEntity<List<FuelSearchResult>> searchFuels(
            @RequestBody FuelSearchRequest request
    ) {
        List<FuelSearchResult> fuelSearchResults = fuelService.searchFuels(request);
        return new ResponseEntity<>(fuelSearchResults, HttpStatus.OK);
    }

    @GetMapping(path = "fuelStations/{fuelStationId}")
    public ResponseEntity<List<Fuel>> getFuelsByFuelStationId(
            @PathVariable(name = "fuelStationId") Integer fuelStationId
    ) {
        List<Fuel> fuels = fuelService.getFuelsByFuelStationId(fuelStationId);
        return new ResponseEntity<>(fuels, HttpStatus.OK);
    }

    @PostMapping(path = "fuelStations/{fuelStationId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Fuel> createFuel(
            @PathVariable(name = "fuelStationId") Integer fuelStationId,
            @RequestBody FuelCreateRequest fuel
    ) {
        fuelService.createFuel(fuelStationId, fuel);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping(path = "{fuelId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Fuel> updateFuel(
            @PathVariable(name = "fuelId") Integer fuelId,
            @RequestBody FuelUpdateRequest fuel
    ) {
        fuelService.updateFuel(fuelId, fuel);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "{fuelId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Fuel> deleteFuel(
            @PathVariable(name = "fuelId") Integer fuelId
    ) {
        fuelService.deleteFuel(fuelId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

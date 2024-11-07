package com.dimitriskikidis.fuelappserver.fuelstation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/fuelStations")
@RequiredArgsConstructor
public class FuelStationController {

    private final FuelStationService fuelStationService;

    @GetMapping(path = "{fuelStationId}")
    public ResponseEntity<FuelStation> getFuelStationById(
            @PathVariable(name = "fuelStationId") Integer fuelStationId
    ) {
        FuelStation fuelStation = fuelStationService.getFuelStationById(fuelStationId);
        return new ResponseEntity<>(fuelStation, HttpStatus.OK);
    }

    @GetMapping(path = "owners/{ownerId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<FuelStation> getFuelStationByOwnerId(
            @PathVariable(name = "ownerId") Integer ownerId
    ) {
        FuelStation fuelStation = fuelStationService.getFuelStationByOwnerId(ownerId);
        return new ResponseEntity<>(fuelStation, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<FuelStationCreateResponse> createFuelStation(
            @PathVariable(name = "ownerId") Integer ownerId,
            @RequestBody FuelStationCreateUpdateRequest fuelStation
    ) {
        FuelStationCreateResponse response = fuelStationService.createFuelStation(ownerId, fuelStation);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(path = "{fuelStationId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<FuelStation> updateFuelStation(
            @PathVariable(name = "fuelStationId") Integer fuelStationId,
            @RequestBody FuelStationCreateUpdateRequest fuelStation
    ) {
        fuelStationService.updateFuelStation(fuelStationId, fuelStation);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

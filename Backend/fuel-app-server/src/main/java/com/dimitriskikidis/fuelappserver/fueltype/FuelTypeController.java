package com.dimitriskikidis.fuelappserver.fueltype;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/fuelTypes")
@RequiredArgsConstructor
public class FuelTypeController {

    private final FuelTypeService fuelTypeService;

    @GetMapping
    public ResponseEntity<List<FuelType>> getFuelTypes() {
        List<FuelType> fuelTypes = fuelTypeService.getFuelTypes();
        return new ResponseEntity<>(fuelTypes, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FuelType> createFuelType(
            @RequestBody FuelTypeCreateUpdateRequest request
    ) {
        fuelTypeService.createFuelType(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping(path = "{fuelTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FuelType> updateFuelType(
            @PathVariable(name = "fuelTypeId") Integer fuelTypeId,
            @RequestBody FuelTypeCreateUpdateRequest request
    ) {
        fuelTypeService.updateFuelType(fuelTypeId, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "{fuelTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FuelType> deleteFuelType(
            @PathVariable(name = "fuelTypeId") Integer fuelTypeId
    ) {
        fuelTypeService.deleteFuelType(fuelTypeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

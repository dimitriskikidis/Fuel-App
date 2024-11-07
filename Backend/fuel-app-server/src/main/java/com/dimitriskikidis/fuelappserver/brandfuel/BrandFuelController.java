package com.dimitriskikidis.fuelappserver.brandfuel;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/brandFuels")
@RequiredArgsConstructor
public class BrandFuelController {

    private final BrandFuelService brandFuelService;

    @GetMapping
    public ResponseEntity<List<BrandFuel>> getBrandFuels() {
        List<BrandFuel> brandFuels = brandFuelService.getBrandFuels();
        return new ResponseEntity<>(brandFuels, HttpStatus.OK);
    }

    @GetMapping(path = "fuelStations/{fuelStationId}")
    public ResponseEntity<List<BrandFuel>> getBrandFuelsByFuelStationId(
            @PathVariable(name = "fuelStationId") Integer fuelStationId
    ) {
        List<BrandFuel> brandFuels = brandFuelService.getBrandFuelsByFuelStationId(fuelStationId);
        return new ResponseEntity<>(brandFuels, HttpStatus.OK);
    }

    @PutMapping(path = "{brandFuelId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BrandFuel> updateBrandFuel(
            @PathVariable(name = "brandFuelId") Integer brandFuelId,
            @RequestBody BrandFuelUpdateRequest request
    ) {
        brandFuelService.updateBrandFuel(brandFuelId, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

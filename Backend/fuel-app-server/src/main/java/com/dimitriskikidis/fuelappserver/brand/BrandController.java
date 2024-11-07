package com.dimitriskikidis.fuelappserver.brand;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<List<Brand>> getBrands() {
        List<Brand> brands = brandService.getBrands();
        return new ResponseEntity<>(brands, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Brand> createBrand(
            @RequestBody BrandCreateUpdateRequest request
    ) {
        brandService.createBrand(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping(path = "{brandId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Brand> updateBrand(
            @PathVariable(name = "brandId") Integer brandId,
            @RequestBody BrandCreateUpdateRequest request
    ) {
        brandService.updateBrand(brandId, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "{brandId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Brand> deleteBrand(
            @PathVariable(name = "brandId") Integer brandId
    ) {
        brandService.deleteBrand(brandId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

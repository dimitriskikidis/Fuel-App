package com.dimitriskikidis.fuelappserver.brand;

import com.dimitriskikidis.fuelappserver.brandfuel.BrandFuel;
import com.dimitriskikidis.fuelappserver.brandfuel.BrandFuelRepository;
import com.dimitriskikidis.fuelappserver.fuel.FuelRepository;
import com.dimitriskikidis.fuelappserver.fuelstation.FuelStationRepository;
import com.dimitriskikidis.fuelappserver.fueltype.FuelType;
import com.dimitriskikidis.fuelappserver.fueltype.FuelTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final BrandFuelRepository brandFuelRepository;
    private final FuelStationRepository fuelStationRepository;
    private final FuelRepository fuelRepository;

    public List<Brand> getBrands() {
        return brandRepository.getBrandsSortedByNameAsc();
    }

    @Transactional
    public void createBrand(BrandCreateUpdateRequest request) {
        String name = request.name();
        if (brandRepository.existsByName(name)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This brand already exists."
            );
        }

        Brand brand = new Brand(
                name,
                request.iconBytes()
        );
        brand = brandRepository.save(brand);

        List<FuelType> fuelTypes = fuelTypeRepository.findAll();
        List<BrandFuel> brandFuels = new ArrayList<>();
        Integer brandId = brand.getId();

        for (FuelType fuelType : fuelTypes) {
            String brandFuelName = String.format("%s", fuelType.getName());
            BrandFuel brandFuel = new BrandFuel(
                    brandId,
                    fuelType.getId(),
                    brandFuelName,
                    false
            );
            brandFuels.add(brandFuel);
        }
        brandFuelRepository.saveAll(brandFuels);
    }

    public void updateBrand(Integer brandId, BrandCreateUpdateRequest request) {
        Optional<Brand> optionalBrand = brandRepository.findById(brandId);
        if (optionalBrand.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "The brand was not found."
            );
        }

        String newName = request.name();
        boolean nameExists = brandRepository.existsByNameAndIdNot(newName, brandId);
        if (nameExists) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This brand already exists."
            );
        }

        Brand brand = optionalBrand.get();
        brand.setName(newName);
        brand.setIconBytes(request.iconBytes());
        brandRepository.save(brand);
    }

    @Transactional
    public void deleteBrand(Integer brandId) {
        if (!brandRepository.existsById(brandId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "The brand was not found."
            );
        }

        Brand newBrand = brandRepository.getBrandsSortedByNameAsc()
                .stream()
                .filter(b -> !b.getId().equals(brandId))
                .findFirst()
                .orElse(null);

        if (newBrand == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "There are no other brands remaining."
            );
        }
        Integer newBrandId = newBrand.getId();

        fuelRepository.deleteByBrandId(brandId);
        fuelStationRepository.updateBrandByBrandId(newBrandId, brandId);
        brandFuelRepository.deleteByBrandId(brandId);
        brandRepository.deleteById(brandId);
    }
}

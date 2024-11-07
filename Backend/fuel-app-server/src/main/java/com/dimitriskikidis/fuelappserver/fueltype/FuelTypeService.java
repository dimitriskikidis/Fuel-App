package com.dimitriskikidis.fuelappserver.fueltype;

import com.dimitriskikidis.fuelappserver.brand.Brand;
import com.dimitriskikidis.fuelappserver.brand.BrandRepository;
import com.dimitriskikidis.fuelappserver.brandfuel.BrandFuel;
import com.dimitriskikidis.fuelappserver.brandfuel.BrandFuelRepository;
import com.dimitriskikidis.fuelappserver.fuel.FuelRepository;
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
public class FuelTypeService {

    private final BrandRepository brandRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final BrandFuelRepository brandFuelRepository;
    private final FuelRepository fuelRepository;

    public List<FuelType> getFuelTypes() {
        return fuelTypeRepository.getFuelTypesSortedByNameAsc();
    }

    @Transactional
    public void createFuelType(FuelTypeCreateUpdateRequest request) {
        String name = request.name();
        if (fuelTypeRepository.existsByName(name)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This fuel type already exists."
            );
        }

        FuelType fuelType = new FuelType(name);
        fuelType = fuelTypeRepository.save(fuelType);

        List<Brand> brands = brandRepository.findAll();
        List<BrandFuel> brandFuels = new ArrayList<>();
        Integer fuelTypeId = fuelType.getId();

        for (Brand brand : brands) {
            String brandFuelName = String.format("%s", fuelType.getName());
            BrandFuel brandFuel = new BrandFuel(
                    brand.getId(),
                    fuelTypeId,
                    brandFuelName,
                    false
            );
            brandFuels.add(brandFuel);
        }
        brandFuelRepository.saveAll(brandFuels);
    }

    public void updateFuelType(Integer fuelTypeId, FuelTypeCreateUpdateRequest request) {
        Optional<FuelType> optionalFuelType = fuelTypeRepository.findById(fuelTypeId);
        if (optionalFuelType.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "The fuel type was not found."
            );
        }

        String newName = request.name();
        boolean nameExists = fuelTypeRepository.existsByNameAndIdNot(newName, fuelTypeId);
        if (nameExists) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This fuel type already exists."
            );
        }

        FuelType fuelType = optionalFuelType.get();
        fuelType.setName(newName);
        fuelTypeRepository.save(fuelType);
    }

    @Transactional
    public void deleteFuelType(Integer fuelTypeId) {
        if (!fuelTypeRepository.existsById(fuelTypeId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "The fuel type was not found."
            );
        }

        fuelRepository.deleteByFuelTypeId(fuelTypeId);
        brandFuelRepository.deleteByFuelTypeId(fuelTypeId);
        fuelTypeRepository.deleteById(fuelTypeId);
    }
}

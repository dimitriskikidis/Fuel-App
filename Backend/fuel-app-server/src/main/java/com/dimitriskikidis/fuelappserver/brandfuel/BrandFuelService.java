package com.dimitriskikidis.fuelappserver.brandfuel;

import com.dimitriskikidis.fuelappserver.fuel.FuelRepository;
import com.dimitriskikidis.fuelappserver.fuelstation.FuelStation;
import com.dimitriskikidis.fuelappserver.fuelstation.FuelStationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrandFuelService {

    private final BrandFuelRepository brandFuelRepository;
    private final FuelStationRepository fuelStationRepository;
    private final FuelRepository fuelRepository;

    public List<BrandFuel> getBrandFuels() {
        return brandFuelRepository.findAll();
    }

    public List<BrandFuel> getBrandFuelsByFuelStationId(Integer fuelStationId) {
        Optional<FuelStation> optionalFuelStation = fuelStationRepository.findById(fuelStationId);
        if (optionalFuelStation.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "The fuel station was not found."
            );
        }

        FuelStation fuelStation = optionalFuelStation.get();
        Integer brandId = fuelStation.getBrandId();
        return brandFuelRepository.findByBrandIdAndIsEnabled(brandId, true);
    }

    @Transactional
    public void updateBrandFuel(Integer brandFuelId, BrandFuelUpdateRequest request) {
        Optional<BrandFuel> optionalBrandFuel = brandFuelRepository.findById(brandFuelId);
        if (optionalBrandFuel.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "The brand fuel was not found."
            );
        }

        BrandFuel brandFuel = optionalBrandFuel.get();

        if (!request.isEnabled()) {
            fuelRepository.deleteByBrandIdAndFuelTypeId(brandFuel.getBrandId(), brandFuel.getFuelTypeId());
        }

        brandFuel.setName(request.name());
        brandFuel.setIsEnabled(request.isEnabled());
        brandFuel = brandFuelRepository.save(brandFuel);

        fuelRepository.updateNameByBrandIdAndFuelTypeId(
                brandFuel.getName(),
                brandFuel.getBrandId(),
                brandFuel.getFuelTypeId()
        );
    }
}

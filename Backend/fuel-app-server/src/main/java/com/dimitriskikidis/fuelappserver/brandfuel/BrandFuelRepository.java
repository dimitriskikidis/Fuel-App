package com.dimitriskikidis.fuelappserver.brandfuel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandFuelRepository extends JpaRepository<BrandFuel, Integer> {

    List<BrandFuel> findByBrandIdAndIsEnabled(Integer brandId, Boolean isEnabled);

    void deleteByBrandId(Integer brandId);

    void deleteByFuelTypeId(Integer fuelTypeId);
}

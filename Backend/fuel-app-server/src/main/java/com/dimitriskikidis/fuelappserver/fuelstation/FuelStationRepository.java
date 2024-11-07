package com.dimitriskikidis.fuelappserver.fuelstation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FuelStationRepository extends JpaRepository<FuelStation, Integer> {

    @Modifying
    @Query("update FuelStation fs set fs.brandId = ?1 where fs.brandId = ?2")
    void updateBrandByBrandId(Integer newBrandId, Integer oldBrandId);
}

package com.dimitriskikidis.fuelappserver.fuel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuelRepository extends JpaRepository<Fuel, Integer> {

    List<Fuel> findByFuelStationId(Integer fuelStationId);

    @Modifying
    @Query("delete from Fuel f where f.fuelStation.id in " +
            "(select fs.id from FuelStation fs where fs.brandId = ?1)")
    void deleteByBrandId(Integer brandId);

    @Modifying
    @Query("delete from Fuel f where f.fuelTypeId = ?1")
    void deleteByFuelTypeId(Integer fuelTypeId);

    @Modifying
    @Query("delete from Fuel f where f.fuelStation.id = ?1")
    void deleteByFuelStationId(Integer fuelStationId);

    @Modifying
    @Query("delete from Fuel f where " +
            "f.fuelTypeId = ?2 and f.fuelStation.id in " +
            "(select fs.id from FuelStation fs where fs.brandId = ?1)")
    void deleteByBrandIdAndFuelTypeId(Integer brandId, Integer fuelTypeId);

    @Modifying
    @Query("update Fuel f set f.name = ?1 where " +
            "f.fuelStation.id in (select fs.id from FuelStation fs where fs.brandId = ?2) and " +
            "f.fuelTypeId = ?3")
    void updateNameByBrandIdAndFuelTypeId(String name, Integer brandId, Integer fuelTypeId);
}

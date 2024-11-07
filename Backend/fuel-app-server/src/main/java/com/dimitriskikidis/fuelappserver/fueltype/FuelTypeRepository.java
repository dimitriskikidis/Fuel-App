package com.dimitriskikidis.fuelappserver.fueltype;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuelTypeRepository extends JpaRepository<FuelType, Integer> {

    @Query("select ft from FuelType ft order by ft.name asc")
    List<FuelType> getFuelTypesSortedByNameAsc();

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer fuelTypeId);
}

package com.dimitriskikidis.fuelappserver.brand;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {

    @Query("select b from Brand b order by b.name asc")
    List<Brand> getBrandsSortedByNameAsc();

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer brandId);
}

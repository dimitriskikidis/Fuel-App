package com.dimitriskikidis.fuelappserver.brandfuel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "BrandFuel")
@Table(
        name = "brand_fuels",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UQ_BrandFuels_BrandId_FuelTypeId",
                        columnNames = {
                                "brandId",
                                "fuelTypeId"
                        }
                )
        }
)
public class BrandFuel {

    @Id
    @SequenceGenerator(
            name = "brand_fuels_id_sequence",
            sequenceName = "brand_fuels_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "brand_fuels_id_sequence"
    )
    private Integer id;

    @Column(
            nullable = false
    )
    private Integer brandId;

    @Column(
            nullable = false
    )
    private Integer fuelTypeId;

    @Column(
            nullable = false,
            length = 50
    )
    private String name;

    @Column(
            nullable = false
    )
    private Boolean isEnabled;

    public BrandFuel(Integer brandId, Integer fuelTypeId, String name, Boolean isEnabled) {
        this.brandId = brandId;
        this.fuelTypeId = fuelTypeId;
        this.name = name;
        this.isEnabled = isEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BrandFuel brandFuel = (BrandFuel) o;
        return id != null && id.equals(brandFuel.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

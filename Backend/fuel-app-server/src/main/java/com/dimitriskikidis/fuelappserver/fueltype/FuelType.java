package com.dimitriskikidis.fuelappserver.fueltype;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "FuelType")
@Table(
        name = "fuel_types",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UQ_FuelTypes_Name",
                        columnNames = "name"
                )
        }
)
public class FuelType {

    @Id
    @SequenceGenerator(
            name = "fuel_types_id_sequence",
            sequenceName = "fuel_types_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "fuel_types_id_sequence"
    )
    private Integer id;

    @Column(
            nullable = false,
            length = 30
    )
    private String name;

    public FuelType(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FuelType fuelType = (FuelType) o;
        return id != null && id.equals(fuelType.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

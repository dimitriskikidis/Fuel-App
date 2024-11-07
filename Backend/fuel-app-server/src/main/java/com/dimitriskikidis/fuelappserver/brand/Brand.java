package com.dimitriskikidis.fuelappserver.brand;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "Brand")
@Table(
        name = "brands",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UQ_Brands_Name",
                        columnNames = "name"
                )
        }
)
public class Brand {

    @Id
    @SequenceGenerator(
            name = "brands_id_sequence",
            sequenceName = "brands_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "brands_id_sequence"
    )
    private Integer id;

    @Column(
            nullable = false,
            length = 30
    )
    private String name;

    @Column(
            nullable = false,
            length = 65535
    )
    @Lob
    private byte[] iconBytes;

    public Brand(String name, byte[] iconBytes) {
        this.name = name;
        this.iconBytes = iconBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Brand brand = (Brand) o;
        return id != null && id.equals(brand.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

package com.dimitriskikidis.fuelappserver.owner;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "Owner")
@Table(
        name = "owners",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UQ_Owners_UserId",
                        columnNames = "userId"
                ),
                @UniqueConstraint(
                        name = "UQ_Owners_FuelStationId",
                        columnNames = "fuelStationId"
                )
        }
)
public class Owner {

    @Id
    @SequenceGenerator(
            name = "owners_id_sequence",
            sequenceName = "owners_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "owners_id_sequence"
    )
    private Integer id;

    @Column(
            nullable = false
    )
    private Integer userId;

    @Column(
            nullable = false,
            length = 50
    )
    private String firstName;

    @Column(
            nullable = false,
            length = 50
    )
    private String lastName;

    @Column
    private Integer fuelStationId;

    public Owner(Integer userId,
                 String firstName,
                 String lastName) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Owner owner = (Owner) o;
        return id != null && id.equals(owner.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

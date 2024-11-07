package com.dimitriskikidis.fuelappserver.fuel;

import com.dimitriskikidis.fuelappserver.fuelstation.FuelStation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "Fuel")
@Table(
        name = "fuels",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UQ_Fuels_FuelStationId_FuelTypeId",
                        columnNames = {
                                "fuel_station_id",
                                "fuelTypeId"
                        }
                )
        }
)
public class Fuel {

    @Id
    @SequenceGenerator(
            name = "fuels_id_sequence",
            sequenceName = "fuels_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "fuels_id_sequence"
    )
    private Integer id;

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
    private Integer price;

    @Column(
            nullable = false,
            columnDefinition = "datetime"
    )
    private LocalDateTime lastUpdate;

    @ManyToOne(
            optional = false,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "fuel_station_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_Fuels_FuelStations"),
            nullable = false
    )
    @JsonIgnore
    private FuelStation fuelStation;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("fuelStationId")
    private Integer _fuelStationId;

    public Fuel(Integer fuelTypeId,
                String name,
                Integer price,
                LocalDateTime lastUpdate,
                FuelStation fuelStation) {
        this.fuelTypeId = fuelTypeId;
        this.name = name;
        this.price = price;
        this.lastUpdate = lastUpdate;
        this.fuelStation = fuelStation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fuel fuel = (Fuel) o;
        return id != null && id.equals(fuel.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

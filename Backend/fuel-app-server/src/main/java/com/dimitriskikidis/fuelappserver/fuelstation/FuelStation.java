package com.dimitriskikidis.fuelappserver.fuelstation;

import com.dimitriskikidis.fuelappserver.brand.Brand;
import com.dimitriskikidis.fuelappserver.fuel.Fuel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "FuelStation")
@Table(
        name = "fuel_stations",
        indexes = {
                @Index(
                        name = "IX_FuelStations_BrandId",
                        columnList = "brandId"
                ),
                @Index(
                        name = "IX_FuelStations_Latitude",
                        columnList = "latitude"
                ),
                @Index(
                        name = "IX_FuelStations_Longitude",
                        columnList = "longitude"
                )
        }
)
public class FuelStation {

    @Id
    @SequenceGenerator(
            name = "fuel_stations_id_sequence",
            sequenceName = "fuel_stations_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "fuel_stations_id_sequence"
    )
    private Integer id;

    @Column(
            nullable = false
    )
    private Integer brandId;

    @Column(
            nullable = false
    )
    private Double latitude;

    @Column(
            nullable = false
    )
    private Double longitude;

    @Column(
            nullable = false,
            length = 100
    )
    private String name;

    @Column(
            nullable = false,
            length = 100
    )
    private String city;

    @Column(
            nullable = false,
            length = 100
    )
    private String address;

    @Column(
            nullable = false,
            columnDefinition = "char(5)"
    )
    private String postalCode;

    @Column(
            nullable = false,
            columnDefinition = "char(10)"
    )
    private String phoneNumber;

    @OneToMany(
            mappedBy = "fuelStation",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnore
    private List<Fuel> fuels;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Float rating;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Brand brand;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer reviewCount;

    public FuelStation(Integer brandId,
                       Double latitude,
                       Double longitude,
                       String name,
                       String city,
                       String address,
                       String postalCode,
                       String phoneNumber) {
        this.brandId = brandId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.city = city;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.fuels = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FuelStation fuelStation = (FuelStation) o;
        return id != null && id.equals(fuelStation.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public void addFuel(Fuel fuel) {
        fuels.add(fuel);
    }

    public void removeFuel(Fuel fuel) {
        fuels.remove(fuel);
    }
}

package com.dimitriskikidis.fuelappserver.review;

import com.dimitriskikidis.fuelappserver.fuelstation.FuelStation;
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
@Entity(name = "Review")
@Table(
        name = "reviews",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UQ_Reviews_FuelStationId_ConsumerId",
                        columnNames = {
                                "fuelStationId",
                                "consumerId"
                        }
                )
        }
)
public class Review {

    @Id
    @SequenceGenerator(
            name = "reviews_id_sequence",
            sequenceName = "reviews_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "reviews_id_sequence"
    )
    private Integer id;

    @Column(
            nullable = false
    )
    private Integer fuelStationId;

    @Column(
            nullable = false
    )
    private Integer consumerId;

    @Column(
            nullable = false
    )
    @JsonProperty("rating")
    private Integer reviewRating;

    @Column(
            nullable = false
    )
    @JsonProperty("text")
    private String reviewText;

    @Column(
            nullable = false,
            columnDefinition = "datetime"
    )
    private LocalDateTime lastUpdate;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String username;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private FuelStation fuelStation;

    public Review(Integer fuelStationId,
                  Integer consumerId,
                  Integer reviewRating,
                  String reviewText,
                  LocalDateTime lastUpdate) {
        this.fuelStationId = fuelStationId;
        this.consumerId = consumerId;
        this.reviewRating = reviewRating;
        this.reviewText = reviewText;
        this.lastUpdate = lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return id != null && id.equals(review.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

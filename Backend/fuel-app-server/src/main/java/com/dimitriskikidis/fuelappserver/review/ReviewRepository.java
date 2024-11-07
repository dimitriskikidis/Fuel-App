package com.dimitriskikidis.fuelappserver.review;

import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    @Query("select r, c.username " +
            "from Review r join Consumer c on r.consumerId = c.id " +
            "where r.fuelStationId = ?1 " +
            "order by r.lastUpdate desc")
    List<Tuple> getReviewsWithUsernamesByFuelStationId(Integer fuelStationId);

    @Query("select r, fs " +
            "from Review r join FuelStation fs on r.fuelStationId = fs.id " +
            "where r.consumerId = ?1 " +
            "order by r.lastUpdate desc")
    List<Tuple> getReviewsWithFuelStationsByConsumerId(Integer consumerId);

    @Query("select r.fuelStationId as fuelStationId, avg(r.reviewRating) as rating, count(r.id) as reviewCount " +
            "from Review as r " +
            "where r.fuelStationId in ?1 " +
            "group by r.fuelStationId")
    List<ReviewSummary> getReviewSummariesByFuelStationIds(List<Integer> fuelStationIds);

    @Query("select r.fuelStationId as fuelStationId, avg(r.reviewRating) as rating, count(r.id) as reviewCount " +
            "from Review as r " +
            "where r.fuelStationId = ?1")
    ReviewSummary getReviewSummaryByFuelStationId(Integer fuelStationId);
}

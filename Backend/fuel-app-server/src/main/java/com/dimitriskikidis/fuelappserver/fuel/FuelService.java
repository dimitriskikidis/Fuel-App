package com.dimitriskikidis.fuelappserver.fuel;

import com.dimitriskikidis.fuelappserver.brandfuel.BrandFuel;
import com.dimitriskikidis.fuelappserver.brandfuel.BrandFuelRepository;
import com.dimitriskikidis.fuelappserver.fuelstation.FuelStation;
import com.dimitriskikidis.fuelappserver.fuelstation.FuelStationRepository;
import com.dimitriskikidis.fuelappserver.review.ReviewRepository;
import com.dimitriskikidis.fuelappserver.review.ReviewSummary;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.StreamUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FuelService {

    private final BrandFuelRepository brandFuelRepository;
    private final FuelStationRepository fuelStationRepository;
    private final FuelRepository fuelRepository;
    private final ReviewRepository reviewRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<FuelSearchResult> searchFuels(FuelSearchRequest request) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createQuery(Tuple.class);
        List<Predicate> predicates = new ArrayList<>();

        Root<Fuel> fuel = criteriaQuery.from(Fuel.class);
        Join<Fuel, FuelStation> fuelStation = fuel.join("fuelStation");

        Predicate latitudePredicate = criteriaBuilder
                .between(fuelStation.get("latitude"), request.minLatitude(), request.maxLatitude());
        predicates.add(latitudePredicate);

        Predicate longitudePredicate = criteriaBuilder
                .between(fuelStation.get("longitude"), request.minLongitude(), request.maxLongitude());
        predicates.add(longitudePredicate);

        Predicate fuelTypePredicate = criteriaBuilder
                .equal(fuel.get("fuelTypeId"), request.fuelTypeId());
        predicates.add(fuelTypePredicate);

        if (request.brandIds().size() > 0) {
            Predicate brandPredicate = criteriaBuilder
                    .in(fuelStation.get("brandId"))
                    .value(request.brandIds());
            predicates.add(brandPredicate);
        }

        criteriaQuery
                .multiselect(fuel, fuelStation)
                .where(
                        criteriaBuilder.and(predicates.toArray(new Predicate[0]))
                );

        List<Tuple> resultList = entityManager
                .createQuery(criteriaQuery)
                .getResultList();

        List<Fuel> fuels = resultList.stream()
                .map(tuple -> tuple.get(0, Fuel.class)).toList();

        List<FuelStation> fuelStations = resultList.stream()
                .map(tuple -> tuple.get(1, FuelStation.class)).toList();

        List<Integer> fuelStationIds = fuelStations.stream()
                .map(FuelStation::getId).toList();

        List<ReviewSummary> reviewSummaries = reviewRepository.getReviewSummariesByFuelStationIds(fuelStationIds);

        for (FuelStation fs : fuelStations) {
            Optional<ReviewSummary> optionalReviewSummary = reviewSummaries.stream()
                    .filter(reviewSummary -> reviewSummary.getFuelStationId().equals(fs.getId()))
                    .findFirst();

            if (optionalReviewSummary.isPresent()) {
                ReviewSummary reviewSummary = optionalReviewSummary.get();
                Float rating = BigDecimal
                        .valueOf(reviewSummary.getRating())
                        .setScale(1, RoundingMode.HALF_UP)
                        .floatValue();
                fs.setRating(rating);
                fs.setReviewCount(reviewSummary.getReviewCount());
            }
        }

        List<FuelSearchResult> fuelSearchResultList = StreamUtils.zip(
                fuels.stream(),
                fuelStations.stream(),
                FuelSearchResult::new
        ).toList();

        for (FuelSearchResult fuelSearchResult : fuelSearchResultList) {
            fuelSearchResult.fuel().set_fuelStationId(fuelSearchResult.fuelStation().getId());
        }

        return fuelSearchResultList;
    }

    public List<Fuel> getFuelsByFuelStationId(Integer fuelStationId) {
        return fuelRepository.findByFuelStationId(fuelStationId);
    }

    public void createFuel(Integer fuelStationId, FuelCreateRequest request) {
        Optional<FuelStation> optionalFuelStation = fuelStationRepository.findById(fuelStationId);
        if (optionalFuelStation.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The fuel station was not found."
            );
        }
        FuelStation fuelStation = optionalFuelStation.get();

        Optional<BrandFuel> optionalBrandFuel = brandFuelRepository.findById(request.brandFuelId());
        if (optionalBrandFuel.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The brand fuel was not found."
            );
        }
        BrandFuel brandFuel = optionalBrandFuel.get();

        Fuel fuel = new Fuel(
                brandFuel.getFuelTypeId(),
                brandFuel.getName(),
                request.price(),
                LocalDateTime.now(ZoneOffset.UTC),
                fuelStation
        );
        fuelStation.addFuel(fuel);
        fuelStationRepository.save(fuelStation);
    }

    public void updateFuel(Integer fuelId, FuelUpdateRequest request) {
        Optional<Fuel> optionalFuel = fuelRepository.findById(fuelId);
        if (optionalFuel.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "The fuel was not found."
            );
        }
        Fuel fuel = optionalFuel.get();

        fuel.setPrice(request.price());
        fuel.setLastUpdate(LocalDateTime.now(ZoneOffset.UTC));
        fuelRepository.save(fuel);
    }

    @Transactional
    public void deleteFuel(Integer fuelId) {
        Optional<Fuel> optionalFuel = fuelRepository.findById(fuelId);
        if (optionalFuel.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "The fuel was not found."
            );
        }

        Fuel fuel = optionalFuel.get();
        FuelStation fuelStation = fuel.getFuelStation();
        fuelStation.removeFuel(fuel);
        fuel.setFuelStation(null);
        fuelStationRepository.save(fuelStation);
    }
}

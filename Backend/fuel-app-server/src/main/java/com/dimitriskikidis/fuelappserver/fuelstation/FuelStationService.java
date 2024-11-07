package com.dimitriskikidis.fuelappserver.fuelstation;

import com.dimitriskikidis.fuelappserver.brand.Brand;
import com.dimitriskikidis.fuelappserver.brand.BrandRepository;
import com.dimitriskikidis.fuelappserver.fuel.Fuel;
import com.dimitriskikidis.fuelappserver.fuel.FuelRepository;
import com.dimitriskikidis.fuelappserver.owner.Owner;
import com.dimitriskikidis.fuelappserver.owner.OwnerRepository;
import com.dimitriskikidis.fuelappserver.review.Review;
import com.dimitriskikidis.fuelappserver.review.ReviewRepository;
import com.dimitriskikidis.fuelappserver.review.ReviewSummary;
import com.dimitriskikidis.fuelappserver.user.User;
import com.dimitriskikidis.fuelappserver.user.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FuelStationService {

    private final OwnerRepository ownerRepository;
    private final BrandRepository brandRepository;
    private final FuelStationRepository fuelStationRepository;
    private final FuelRepository fuelRepository;
    private final ReviewRepository reviewRepository;

    public FuelStation getFuelStationById(Integer fuelStationId) {
        Optional<FuelStation> optionalFuelStation = fuelStationRepository.findById(fuelStationId);
        if (optionalFuelStation.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "The fuel station was not found."
            );
        }
        FuelStation fuelStation = optionalFuelStation.get();

        Optional<Brand> optionalBrand = brandRepository.findById(fuelStation.getBrandId());
        if (optionalBrand.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "The brand was not found."
            );
        }
        Brand brand = optionalBrand.get();
        fuelStation.setBrand(brand);

        ReviewSummary reviewSummary = reviewRepository.getReviewSummaryByFuelStationId(fuelStationId);
        if (reviewSummary.getFuelStationId() != null) {
            Float rating = BigDecimal
                    .valueOf(reviewSummary.getRating())
                    .setScale(1, RoundingMode.HALF_UP)
                    .floatValue();
            fuelStation.setRating(rating);
            fuelStation.setReviewCount(reviewSummary.getReviewCount());
        }

        return fuelStation;
    }

    public FuelStation getFuelStationByOwnerId(Integer ownerId) {
        Optional<Owner> optionalOwner = ownerRepository.findById(ownerId);
        if (optionalOwner.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED
            );
        }
        Owner owner = optionalOwner.get();

        Integer fuelStationId = owner.getFuelStationId();
        Optional<FuelStation> optionalFuelStation = fuelStationRepository.findById(fuelStationId);

        return optionalFuelStation.orElse(null);
    }

    @Transactional
    public FuelStationCreateResponse createFuelStation(
            Integer ownerId,
            FuelStationCreateUpdateRequest request
    ) {
        Optional<Owner> optionalOwner = ownerRepository.findById(ownerId);
        if (optionalOwner.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED
            );
        }

        boolean brandExists = brandRepository.existsById(request.brandId());
        if (!brandExists) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The brand was not found."
            );
        }

        FuelStation fuelStation = new FuelStation(
                request.brandId(),
                request.latitude(),
                request.longitude(),
                request.name(),
                request.city(),
                request.address(),
                request.postalCode(),
                request.phoneNumber()
        );
        fuelStation = fuelStationRepository.save(fuelStation);

        Owner owner = optionalOwner.get();
        owner.setFuelStationId(fuelStation.getId());
        ownerRepository.save(owner);

        return new FuelStationCreateResponse(fuelStation.getId());
    }

    @Transactional
    public void updateFuelStation(Integer fuelStationId, FuelStationCreateUpdateRequest request) {
        Optional<FuelStation> optionalFuelStation = fuelStationRepository.findById(fuelStationId);
        if (optionalFuelStation.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "The fuel station was not found."
            );
        }
        FuelStation fuelStation = optionalFuelStation.get();

        if (!request.brandId().equals(fuelStation.getBrandId())) {
            fuelRepository.deleteByFuelStationId(fuelStationId);
        }

        fuelStation.setBrandId(request.brandId());
        fuelStation.setLatitude(request.latitude());
        fuelStation.setLongitude(request.longitude());
        fuelStation.setName(request.name());
        fuelStation.setCity(request.city());
        fuelStation.setAddress(request.address());
        fuelStation.setPostalCode(request.postalCode());
        fuelStation.setPhoneNumber(request.phoneNumber());
        fuelStationRepository.save(fuelStation);
    }
}

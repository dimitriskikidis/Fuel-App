package com.dimitriskikidis.fuelappserver.review;

import com.dimitriskikidis.fuelappserver.fuelstation.FuelStation;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public List<Review> getReviewsByFuelStationId(Integer fuelStationId) {
        List<Tuple> resultList = reviewRepository.getReviewsWithUsernamesByFuelStationId(fuelStationId);

        List<Review> reviews = resultList.stream()
                .map(tuple -> tuple.get(0, Review.class)).toList();

        List<String> usernames = resultList.stream()
                .map(tuple -> tuple.get(1, String.class)).toList();

        for (int i = 0; i < resultList.size(); i++) {
            reviews.get(i).setUsername(usernames.get(i));
        }

        return reviews;
    }

    public List<Review> getReviewsWithFuelStationsByConsumerId(Integer consumerId) {
        List<Tuple> resultList = reviewRepository.getReviewsWithFuelStationsByConsumerId(consumerId);

        List<Review> reviews = resultList.stream()
                .map(tuple -> tuple.get(0, Review.class)).toList();

        List<FuelStation> fuelStations = resultList.stream()
                .map(tuple -> tuple.get(1, FuelStation.class)).toList();

        for (int i = 0; i < resultList.size(); i++) {
            reviews.get(i).setFuelStation(fuelStations.get(i));
        }

        return reviews;
    }

    public void createReview(
            Integer fuelStationId,
            Integer consumerId,
            ReviewCreateUpdateRequest request
    ) {
        Review review = new Review(
                fuelStationId,
                consumerId,
                request.rating(),
                request.text(),
                LocalDateTime.now(ZoneOffset.UTC)
        );
        reviewRepository.save(review);
    }

    public void updateReview(Integer reviewId, ReviewCreateUpdateRequest request) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "The review was not found."
            );
        }
        Review review = optionalReview.get();

        review.setReviewRating(request.rating());
        review.setReviewText(request.text());
        review.setLastUpdate(LocalDateTime.now(ZoneOffset.UTC));
        reviewRepository.save(review);
    }

    public void deleteReview(Integer reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "The review was not found."
            );
        }
        reviewRepository.deleteById(reviewId);
    }
}

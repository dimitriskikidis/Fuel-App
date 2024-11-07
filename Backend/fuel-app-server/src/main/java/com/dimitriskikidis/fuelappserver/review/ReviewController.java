package com.dimitriskikidis.fuelappserver.review;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping(path = "fuelStations/{fuelStationId}")
    public ResponseEntity<List<Review>> getReviewsByFuelStationId(
            @PathVariable(name = "fuelStationId") Integer fuelStationId
    ) {
        List<Review> reviews = reviewService.getReviewsByFuelStationId(fuelStationId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping(path = "consumers/{consumerId}")
    public ResponseEntity<List<Review>> getReviewsWithFuelStationsByConsumerId(
            @PathVariable(name = "consumerId") Integer consumerId
    ) {
        List<Review> reviews = reviewService.getReviewsWithFuelStationsByConsumerId(consumerId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @PostMapping(path = "fuelStations/{fuelStationId}/consumers/{consumerId}")
    public ResponseEntity<Review> createReview(
            @PathVariable(name = "fuelStationId") Integer fuelStationId,
            @PathVariable(name = "consumerId") Integer consumerId,
            @RequestBody ReviewCreateUpdateRequest request
    ) {
        reviewService.createReview(fuelStationId, consumerId, request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping(path = "{reviewId}")
    public ResponseEntity<Review> updateReview(
            @PathVariable(name = "reviewId") Integer reviewId,
            @RequestBody ReviewCreateUpdateRequest request
    ) {
        reviewService.updateReview(reviewId, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "{reviewId}")
    public ResponseEntity<Review> deleteReview(
            @PathVariable(name = "reviewId") Integer reviewId
    ) {
        reviewService.deleteReview(reviewId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

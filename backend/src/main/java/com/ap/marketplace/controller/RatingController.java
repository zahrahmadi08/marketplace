package com.ap.marketplace.controller;

import com.ap.marketplace.dto.rating.RatingRequest;
import com.ap.marketplace.dto.rating.RatingResponse;
import com.ap.marketplace.security.CurrentUserProvider;
import com.ap.marketplace.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ads/{adId}/ratings")
public class RatingController {

    private final RatingService ratingService;
    private final CurrentUserProvider currentUser;

    public RatingController(RatingService ratingService, CurrentUserProvider currentUser) {
        this.ratingService = ratingService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public List<RatingResponse> list(@PathVariable Long adId) {
        return ratingService.list(adId);
    }

    @PostMapping
    public ResponseEntity<RatingResponse> rate(@PathVariable Long adId,
                                               @Valid @RequestBody RatingRequest req) {
        RatingResponse r = ratingService.rate(currentUser.require(), adId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(r);
    }
}

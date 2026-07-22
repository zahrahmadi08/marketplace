package com.ap.marketplace.dto.rating;

import com.ap.marketplace.domain.Rating;
import java.time.Instant;

public record RatingResponse(
        Long id, Long raterId, String raterName, int score, String comment, Instant createdAt
) {
    public static RatingResponse from(Rating r) {
        return new RatingResponse(r.getId(), r.getRater().getId(), r.getRater().getFullName(),
                r.getScore(), r.getComment(), r.getCreatedAt());
    }
}

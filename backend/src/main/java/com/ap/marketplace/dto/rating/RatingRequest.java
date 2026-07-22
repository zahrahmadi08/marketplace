package com.ap.marketplace.dto.rating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record RatingRequest(
        @Min(1) @Max(5) int score,
        @Size(max = 500) String comment
) { }

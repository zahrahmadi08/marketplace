package com.ap.marketplace.client.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RatingDto(Long id, Long raterId, String raterName, int score,
                        String comment, String createdAt) { }

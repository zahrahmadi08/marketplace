package com.ap.marketplace.client.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AdDetailDto(Long id, String type, String typeLabel, String title, String description,
                          long price, String cityName, String categoryName, UserDto owner,
                          List<String> imageUrls, String typeSummary, Map<String, Object> typeDetails,
                          double averageRating, int ratingCount, String status, String createdAt) {
    public String priceLabel() { return String.format("%,d تومان", price); }
}

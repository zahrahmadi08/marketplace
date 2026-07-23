package com.ap.marketplace.client.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AdSummaryDto(Long id, String type, String typeLabel, String title, long price,
                           String cityName, String categoryName, String thumbnailUrl,
                           String typeSummary, double averageRating, String status) {
    public String priceLabel() {
        return String.format("%,d تومان", price);
    }
}

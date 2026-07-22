package com.ap.marketplace.dto.ad;

import com.ap.marketplace.domain.Advertisement;

public record AdSummaryResponse(
        Long id, String type, String typeLabel, String title, long price,
        String cityName, String categoryName, String thumbnailUrl,
        String typeSummary, double averageRating, String status
) {
    public static AdSummaryResponse from(Advertisement ad, double averageRating) {
        String thumb = ad.getImages().isEmpty() ? null : ad.getImages().get(0).getUrl();
        return new AdSummaryResponse(
                ad.getId(), ad.type().name(), ad.typeLabel(), ad.getTitle(), ad.getPrice(),
                ad.getCity().getName(), ad.getCategory().getName(), thumb,
                ad.typeSummary(), averageRating, ad.getStatus().name());
    }
}

package com.ap.marketplace.dto.ad;

import com.ap.marketplace.domain.*;
import com.ap.marketplace.dto.auth.UserResponse;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record AdDetailResponse(
        Long id, String type, String typeLabel, String title, String description, long price,
        String cityName, String categoryName, UserResponse owner, List<String> imageUrls,
        String typeSummary, Map<String, Object> typeDetails,
        double averageRating, int ratingCount, String status, Instant createdAt
) {
    public static AdDetailResponse from(Advertisement ad, double averageRating, int ratingCount) {
        List<String> urls = ad.getImages().stream().map(AdImage::getUrl).toList();
        return new AdDetailResponse(
                ad.getId(), ad.type().name(), ad.typeLabel(), ad.getTitle(), ad.getDescription(),
                ad.getPrice(), ad.getCity().getName(), ad.getCategory().getName(),
                UserResponse.from(ad.getOwner()), urls, ad.typeSummary(), typeDetails(ad),
                averageRating, ratingCount, ad.getStatus().name(), ad.getCreatedAt());
    }

    private static Map<String, Object> typeDetails(Advertisement ad) {
        Map<String, Object> m = new LinkedHashMap<>();
        if (ad instanceof VehicleAd v) {
            m.put("brand", v.getBrand());
            m.put("model", v.getModel());
            m.put("productionYear", v.getProductionYear());
            m.put("mileageKm", v.getMileageKm());
        } else if (ad instanceof PropertyAd p) {
            m.put("areaSqm", p.getAreaSqm());
            m.put("rooms", p.getRooms());
            m.put("address", p.getAddress());
            m.put("forRent", p.isForRent());
        } else if (ad instanceof GeneralAd g) {
            m.put("itemCondition", g.getItemCondition());
        }
        return m;
    }
}

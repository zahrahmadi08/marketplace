package com.ap.marketplace.client.api;

import com.ap.marketplace.client.api.dto.RatingDto;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RatingApi {
    private RatingApi() { }

    public static List<RatingDto> list(Long adId) {
        return ApiClient.get("/api/ads/" + adId + "/ratings", new TypeReference<>() { });
    }

    public static RatingDto rate(Long adId, int score, String comment) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("score", score);
        body.put("comment", comment);
        return ApiClient.post("/api/ads/" + adId + "/ratings", body, RatingDto.class);
    }
}

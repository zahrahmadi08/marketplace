package com.ap.marketplace.client.api;

import com.ap.marketplace.client.api.dto.AdSummaryDto;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

public final class FavoriteApi {
    private FavoriteApi() { }

    public static List<AdSummaryDto> list() {
        return ApiClient.get("/api/favorites", new TypeReference<>() { });
    }

    public static void add(Long adId) {
        ApiClient.postNoContent("/api/favorites/" + adId);
    }

    public static void remove(Long adId) {
        ApiClient.delete("/api/favorites/" + adId);
    }
}

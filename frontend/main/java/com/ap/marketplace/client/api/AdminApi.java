package com.ap.marketplace.client.api;

import com.ap.marketplace.client.api.dto.AdSummaryDto;
import com.ap.marketplace.client.api.dto.StatsDto;
import com.ap.marketplace.client.api.dto.UserDto;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

public final class AdminApi {
    private AdminApi() { }

    public static List<AdSummaryDto> pendingAds() {
        return ApiClient.get("/api/admin/ads/pending", new TypeReference<>() { });
    }

    public static void approve(Long adId) { ApiClient.postNoContent("/api/admin/ads/" + adId + "/approve"); }
    public static void reject(Long adId) { ApiClient.postNoContent("/api/admin/ads/" + adId + "/reject"); }

    public static List<UserDto> users() {
        return ApiClient.get("/api/admin/users", new TypeReference<>() { });
    }

    public static void block(Long userId) { ApiClient.postNoContent("/api/admin/users/" + userId + "/block"); }
    public static void unblock(Long userId) { ApiClient.postNoContent("/api/admin/users/" + userId + "/unblock"); }

    public static StatsDto stats() {
        return ApiClient.get("/api/admin/stats", StatsDto.class);
    }
}

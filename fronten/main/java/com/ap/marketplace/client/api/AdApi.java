package com.ap.marketplace.client.api;

import com.ap.marketplace.client.api.dto.AdDetailDto;
import com.ap.marketplace.client.api.dto.AdSummaryDto;
import com.ap.marketplace.client.api.dto.PageDto;
import com.fasterxml.jackson.core.type.TypeReference;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public final class AdApi {
    private AdApi() { }

    /** جست‌وجوی عمومی با فیلترها؛ مقادیر null نادیده گرفته می‌شوند. */
    public static PageDto<AdSummaryDto> search(String query, String type, Long categoryId, Long cityId,
                                               Long minPrice, Long maxPrice, String sort, int page) {
        StringBuilder q = new StringBuilder("/api/ads?page=").append(page).append("&size=12");
        append(q, "query", query);
        append(q, "type", type);
        append(q, "categoryId", categoryId);
        append(q, "cityId", cityId);
        append(q, "minPrice", minPrice);
        append(q, "maxPrice", maxPrice);
        append(q, "sort", sort);
        return ApiClient.get(q.toString(), new TypeReference<>() { });
    }

    public static AdDetailDto detail(Long id) {
        return ApiClient.get("/api/ads/" + id, AdDetailDto.class);
    }

    public static List<AdSummaryDto> mine() {
        return ApiClient.get("/api/ads/mine", new TypeReference<>() { });
    }

    public static AdDetailDto create(Map<String, Object> body) {
        return ApiClient.post("/api/ads", body, AdDetailDto.class);
    }

    public static AdDetailDto update(Long id, Map<String, Object> body) {
        return ApiClient.put("/api/ads/" + id, body, AdDetailDto.class);
    }

    public static void delete(Long id) {
        ApiClient.delete("/api/ads/" + id);
    }

    public static void markSold(Long id) {
        ApiClient.postNoContent("/api/ads/" + id + "/sold");
    }

    private static void append(StringBuilder sb, String key, Object value) {
        if (value == null || value.toString().isBlank()) return;
        sb.append('&').append(key).append('=')
          .append(URLEncoder.encode(value.toString(), StandardCharsets.UTF_8));
    }
}

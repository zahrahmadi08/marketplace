package com.ap.marketplace.client.api;

import com.ap.marketplace.client.api.dto.CategoryDto;
import com.ap.marketplace.client.api.dto.CityDto;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

public final class LookupApi {
    private LookupApi() { }

    public static List<CategoryDto> categories() {
        return ApiClient.get("/api/categories", new TypeReference<>() { });
    }

    public static List<CityDto> cities() {
        return ApiClient.get("/api/cities", new TypeReference<>() { });
    }
}

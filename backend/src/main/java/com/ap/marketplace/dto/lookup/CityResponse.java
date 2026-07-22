package com.ap.marketplace.dto.lookup;

import com.ap.marketplace.domain.City;

public record CityResponse(Long id, String name) {
    public static CityResponse from(City c) { return new CityResponse(c.getId(), c.getName()); }
}

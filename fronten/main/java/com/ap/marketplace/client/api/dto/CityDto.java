package com.ap.marketplace.client.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CityDto(Long id, String name) {
    @Override public String toString() { return name; }
}

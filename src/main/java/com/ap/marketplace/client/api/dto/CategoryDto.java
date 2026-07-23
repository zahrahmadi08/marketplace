package com.ap.marketplace.client.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CategoryDto(Long id, String name, Long parentId) {
    @Override public String toString() { return name; }
}

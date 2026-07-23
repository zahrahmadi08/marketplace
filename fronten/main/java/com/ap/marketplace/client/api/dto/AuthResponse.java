package com.ap.marketplace.client.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthResponse(String token, UserDto user) { }

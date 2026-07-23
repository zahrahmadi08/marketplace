package com.ap.marketplace.client.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MessageDto(Long id, Long senderId, String senderName, String text,
                         boolean seen, String sentAt) { }

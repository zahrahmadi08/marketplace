package com.ap.marketplace.client.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ConversationDto(Long id, Long adId, String adTitle, Long buyerId, String buyerName,
                              Long sellerId, String sellerName, String lastMessage, long unreadCount) { }

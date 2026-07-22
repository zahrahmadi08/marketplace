package com.ap.marketplace.dto.chat;

import com.ap.marketplace.domain.Conversation;

public record ConversationResponse(
        Long id, Long adId, String adTitle, Long buyerId, String buyerName,
        Long sellerId, String sellerName, String lastMessage, long unreadCount
) {
    public static ConversationResponse from(Conversation c, String lastMessage, long unreadCount) {
        return new ConversationResponse(
                c.getId(), c.getAdvertisement().getId(), c.getAdvertisement().getTitle(),
                c.getBuyer().getId(), c.getBuyer().getFullName(),
                c.getSeller().getId(), c.getSeller().getFullName(),
                lastMessage, unreadCount);
    }
}

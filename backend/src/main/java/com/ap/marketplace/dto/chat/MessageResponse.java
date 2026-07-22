package com.ap.marketplace.dto.chat;

import com.ap.marketplace.domain.Message;
import java.time.Instant;

public record MessageResponse(
        Long id, Long senderId, String senderName, String text, boolean seen, Instant sentAt
) {
    public static MessageResponse from(Message m) {
        return new MessageResponse(m.getId(), m.getSender().getId(), m.getSender().getFullName(),
                m.getText(), m.isSeen(), m.getSentAt());
    }
}

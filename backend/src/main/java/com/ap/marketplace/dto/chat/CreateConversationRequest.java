package com.ap.marketplace.dto.chat;

import jakarta.validation.constraints.NotNull;

public record CreateConversationRequest(@NotNull Long adId) { }

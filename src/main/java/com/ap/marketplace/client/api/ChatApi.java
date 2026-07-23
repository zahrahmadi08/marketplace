package com.ap.marketplace.client.api;

import com.ap.marketplace.client.api.dto.ConversationDto;
import com.ap.marketplace.client.api.dto.MessageDto;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ChatApi {
    private ChatApi() { }

    public static List<ConversationDto> conversations() {
        return ApiClient.get("/api/conversations", new TypeReference<>() { });
    }

    public static ConversationDto start(Long adId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("adId", adId);
        return ApiClient.post("/api/conversations", body, ConversationDto.class);
    }

    public static List<MessageDto> messages(Long conversationId) {
        return ApiClient.get("/api/conversations/" + conversationId + "/messages",
                new TypeReference<>() { });
    }

    public static MessageDto send(Long conversationId, String text) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("text", text);
        return ApiClient.post("/api/conversations/" + conversationId + "/messages",
                body, MessageDto.class);
    }
}

package com.ap.marketplace.controller;

import com.ap.marketplace.dto.chat.*;
import com.ap.marketplace.security.CurrentUserProvider;
import com.ap.marketplace.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ChatController {

    private final ChatService chatService;
    private final CurrentUserProvider currentUser;

    public ChatController(ChatService chatService, CurrentUserProvider currentUser) {
        this.chatService = chatService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public List<ConversationResponse> list() {
        return chatService.myConversations(currentUser.require());
    }

    @PostMapping
    public ResponseEntity<ConversationResponse> start(@Valid @RequestBody CreateConversationRequest req) {
        ConversationResponse c = chatService.startConversation(currentUser.require(), req.adId());
        return ResponseEntity.status(HttpStatus.CREATED).body(c);
    }

    @GetMapping("/{id}/messages")
    public List<MessageResponse> messages(@PathVariable Long id) {
        return chatService.messages(currentUser.require(), id);
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<MessageResponse> send(@PathVariable Long id,
                                                @Valid @RequestBody SendMessageRequest req) {
        MessageResponse m = chatService.send(currentUser.require(), id, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(m);
    }
}

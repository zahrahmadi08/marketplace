package com.ap.marketplace.repository;

import com.ap.marketplace.domain.Conversation;
import com.ap.marketplace.domain.Message;
import com.ap.marketplace.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationOrderBySentAtAsc(Conversation conversation);
    long countByConversationAndSeenFalseAndSenderNot(Conversation conversation, User me);
}

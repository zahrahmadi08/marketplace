package com.ap.marketplace.service;

import com.ap.marketplace.domain.*;
import com.ap.marketplace.dto.chat.ConversationResponse;
import com.ap.marketplace.dto.chat.MessageResponse;
import com.ap.marketplace.dto.chat.SendMessageRequest;
import com.ap.marketplace.exception.BadRequestException;
import com.ap.marketplace.exception.ForbiddenException;
import com.ap.marketplace.exception.NotFoundException;
import com.ap.marketplace.repository.ConversationRepository;
import com.ap.marketplace.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AdvertisementService advertisementService;

    public ChatService(ConversationRepository conversationRepository, MessageRepository messageRepository,
                       AdvertisementService advertisementService) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.advertisementService = advertisementService;
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> myConversations(User user) {
        return conversationRepository.findAllForUser(user).stream()
                .map(c -> {
                    List<Message> msgs = c.getMessages();
                    String last = msgs.isEmpty() ? null : msgs.get(msgs.size() - 1).getText();
                    long unread = messageRepository.countByConversationAndSeenFalseAndSenderNot(c, user);
                    return ConversationResponse.from(c, last, unread);
                })
                .toList();
    }

    /** شروع گفت‌وگو با فروشنده آگهی؛ خریدار نمی‌تواند مالک باشد. جفت (ad, buyer) یکتا. */
    public ConversationResponse startConversation(User buyer, Long adId) {
        Advertisement ad = advertisementService.entity(adId);
        User seller = ad.getOwner();
        if (seller.getId().equals(buyer.getId())) {
            throw new BadRequestException("نمی‌توانید برای آگهی خودتان گفت‌وگو بسازید");
        }
        if (buyer.isBlocked() || seller.isBlocked()) {
            throw new ForbiddenException("گفت‌وگو با حساب مسدود ممکن نیست");
        }
        Conversation conversation = conversationRepository.findByAdvertisementAndBuyer(ad, buyer)
                .orElseGet(() -> conversationRepository.save(new Conversation(ad, buyer, seller)));
        return ConversationResponse.from(conversation, null, 0);
    }

    @Transactional
    public List<MessageResponse> messages(User user, Long conversationId) {
        Conversation c = requireParticipant(user, conversationId);
        // پیام‌های طرف مقابل را دیده‌شده علامت می‌زنیم.
        for (Message m : c.getMessages()) {
            if (!m.getSender().getId().equals(user.getId()) && !m.isSeen()) {
                m.setSeen(true);
            }
        }
        return c.getMessages().stream().map(MessageResponse::from).toList();
    }

    public MessageResponse send(User sender, Long conversationId, SendMessageRequest req) {
        Conversation c = requireParticipant(sender, conversationId);
        if (sender.isBlocked()) throw new ForbiddenException("حساب شما مسدود است");
        Message message = messageRepository.save(new Message(c, sender, req.text()));
        return MessageResponse.from(message);
    }

    private Conversation requireParticipant(User user, Long conversationId) {
        Conversation c = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new NotFoundException("گفت‌وگو یافت نشد"));
        if (!c.involves(user)) throw new ForbiddenException("به این گفت‌وگو دسترسی ندارید");
        return c;
    }
}

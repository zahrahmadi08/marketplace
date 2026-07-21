package com.ap.marketplace.domain;

import jakarta.persistence.*;
import java.time.Instant;

/** پیام درون یک گفت‌وگو؛ فیلد seen برای وضعیت دیده‌شدن (امتیاز اضافه). */
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(nullable = false, length = 2000)
    private String text;

    @Column(nullable = false)
    private boolean seen = false;

    @Column(nullable = false, updatable = false)
    private Instant sentAt = Instant.now();

    protected Message() { }

    public Message(Conversation conversation, User sender, String text) {
        this.conversation = conversation;
        this.sender = sender;
        this.text = text;
    }

    public Long getId() { return id; }
    public Conversation getConversation() { return conversation; }
    public User getSender() { return sender; }
    public String getText() { return text; }
    public boolean isSeen() { return seen; }
    public void setSeen(boolean seen) { this.seen = seen; }
    public Instant getSentAt() { return sentAt; }
}

package com.ap.marketplace.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** گفت‌وگوی بین خریدار و فروشنده حول یک آگهی؛ جفت (ad, buyer) یکتا است. */
@Entity
@Table(name = "conversations",
        uniqueConstraints = @UniqueConstraint(name = "uk_conversation_ad_buyer",
                columnNames = {"advertisement_id", "buyer_id"}))
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "advertisement_id")
    private Advertisement advertisement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id")
    private User seller;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sentAt ASC")
    private List<Message> messages;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Conversation() { }

    public Conversation(Advertisement advertisement, User buyer, User seller) {
        this.advertisement = advertisement;
        this.buyer = buyer;
        this.seller = seller;
        messages = new ArrayList<>();
    }

    /** آیا این کاربر یکی از دو طرف گفت‌وگو است. */
    public boolean involves(User user) {
        return buyer.getId().equals(user.getId()) || seller.getId().equals(user.getId());
    }

    public Long getId() { return id; }
    public Advertisement getAdvertisement() { return advertisement; }
    public User getBuyer() { return buyer; }
    public User getSeller() { return seller; }
    public List<Message> getMessages() { return messages; }
    public Instant getCreatedAt() { return createdAt; }
}

package com.ap.marketplace.domain;

import jakarta.persistence.*;
import java.time.Instant;

/** علاقه‌مندی کاربر به یک آگهی؛ جفت (user, ad) یکتا است. */
@Entity
@Table(name = "favorites",
        uniqueConstraints = @UniqueConstraint(name = "uk_favorite_user_ad",
                columnNames = {"user_id", "advertisement_id"}))
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "advertisement_id")
    private Advertisement advertisement;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Favorite() { }

    public Favorite(User user, Advertisement advertisement) {
        this.user = user;
        this.advertisement = advertisement;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Advertisement getAdvertisement() { return advertisement; }
    public Instant getCreatedAt() { return createdAt; }
}

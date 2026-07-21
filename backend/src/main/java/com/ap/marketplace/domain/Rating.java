package com.ap.marketplace.domain;

import jakarta.persistence.*;
import java.time.Instant;

/** امتیاز کاربر به یک آگهی (۱ تا ۵)؛ جفت (rater, ad) یکتا است. */
@Entity
@Table(name = "ratings",
        uniqueConstraints = @UniqueConstraint(name = "uk_rating_rater_ad",
                columnNames = {"rater_id", "advertisement_id"}))
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rater_id")
    private User rater;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "advertisement_id")
    private Advertisement advertisement;

    @Column(nullable = false)
    private int score;

    @Column(length = 500)
    private String comment;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Rating() { }

    public Rating(User rater, Advertisement advertisement, int score, String comment) {
        this.rater = rater;
        this.advertisement = advertisement;
        this.score = score;
        this.comment = comment;
    }

    public Long getId() { return id; }
    public User getRater() { return rater; }
    public Advertisement getAdvertisement() { return advertisement; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Instant getCreatedAt() { return createdAt; }
}

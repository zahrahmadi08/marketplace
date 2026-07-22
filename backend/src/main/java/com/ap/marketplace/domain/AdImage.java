package com.ap.marketplace.domain;

import jakarta.persistence.*;

/** تصویر آگهی؛ چند تصویر با ترتیب مشخص . */
@Entity
@Table(name = "ad_images")
public class AdImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "advertisement_id")
    private Advertisement advertisement;

    /** مسیر یا URL تصویر. */
    @Column(nullable = false, length = 500)
    private String url;

    @Column(nullable = false)
    private int orderIndex;

    protected AdImage() { }

    public AdImage(String url, int orderIndex) {
        this.url = url;
        this.orderIndex = orderIndex;
    }

    public Long getId() { return id; }
    public Advertisement getAdvertisement() { return advertisement; }
    public void setAdvertisement(Advertisement advertisement) { this.advertisement = advertisement; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }
}

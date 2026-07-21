package com.ap.marketplace.domain;

import com.ap.marketplace.domain.enums.AdStatus;
import com.ap.marketplace.domain.enums.AdType;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * پایه انتزاعی آگهی. ارث‌بری تک‌جدولی (SINGLE_TABLE) با ستون تفکیک‌گر ad_type.
 * متدهای انتزاعی typeLabel و typeSummary در هر زیرنوع بازتعریف می‌شوند (چندریختی).
 */
@Entity
@Table(name = "advertisements")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ad_type", discriminatorType = DiscriminatorType.STRING, length = 10)
public abstract class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 4000)
    private String description;

    @Column(nullable = false)
    private long price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AdStatus status = AdStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id")
    private City city;

    @OneToMany(mappedBy = "advertisement", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<AdImage> images = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Advertisement() { }

    protected Advertisement(String title, String description, long price,
                            User owner, Category category, City city) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.owner = owner;
        this.category = category;
        this.city = city;
    }

    /** نوع آگهی به‌صورت enum؛ هر زیرنوع مقدار خود را برمی‌گرداند. */
    public abstract AdType type();

    /** برچسب فارسی نوع آگهی برای نمایش (چندریختی). */
    public abstract String typeLabel();

    /** خلاصه ویژگی‌های اختصاصی نوع (خودرو/ملک/عمومی) برای کارت و جست‌وجو (چندریختی). */
    public abstract String typeSummary();

    public void addImage(AdImage image) {
        image.setAdvertisement(this);
        this.images.add(image);
    }

    public boolean isPubliclyVisible() { return status == AdStatus.ACTIVE; }
    public boolean isOwnedBy(User user) { return owner != null && owner.getId().equals(user.getId()); }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }
    public AdStatus getStatus() { return status; }
    public void setStatus(AdStatus status) { this.status = status; }
    public User getOwner() { return owner; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }
    public List<AdImage> getImages() { return images; }
    public Instant getCreatedAt() { return createdAt; }
}

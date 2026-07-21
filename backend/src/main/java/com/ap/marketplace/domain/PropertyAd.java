package com.ap.marketplace.domain;

import com.ap.marketplace.domain.enums.AdType;
import jakarta.persistence.*;

/** آگهی املاک (فروش یا اجاره). */
@Entity
@DiscriminatorValue("PROPERTY")
public class PropertyAd extends Advertisement {

    private Integer areaSqm;

    private Integer rooms;

    @Column(length = 200)
    private String address;

    /** true یعنی اجاره، false یعنی فروش. */
    private boolean forRent;

    protected PropertyAd() { }

    public PropertyAd(String title, String description, long price,
                      User owner, Category category, City city,
                      Integer areaSqm, Integer rooms, String address, boolean forRent) {
        super(title, description, price, owner, category, city);
        this.areaSqm = areaSqm;
        this.rooms = rooms;
        this.address = address;
        this.forRent = forRent;
    }

    @Override public AdType type() { return AdType.PROPERTY; }
    @Override public String typeLabel() { return forRent ? "املاک (اجاره)" : "املاک (فروش)"; }

    @Override
    public String typeSummary() {
        StringBuilder sb = new StringBuilder();
        if (areaSqm != null) sb.append(areaSqm).append(" متر ");
        if (rooms != null) sb.append("• ").append(rooms).append(" خوابه ");
        sb.append(forRent ? "• اجاره" : "• فروش");
        return sb.toString().trim();
    }

    public Integer getAreaSqm() { return areaSqm; }
    public void setAreaSqm(Integer areaSqm) { this.areaSqm = areaSqm; }
    public Integer getRooms() { return rooms; }
    public void setRooms(Integer rooms) { this.rooms = rooms; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public boolean isForRent() { return forRent; }
    public void setForRent(boolean forRent) { this.forRent = forRent; }
}

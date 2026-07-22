package com.ap.marketplace.domain;

import com.ap.marketplace.domain.enums.AdType;
import jakarta.persistence.*;

/** آگهی خودرو. */
@Entity
@DiscriminatorValue("VEHICLE")
public class VehicleAd extends Advertisement {

    @Column(length = 60)
    private String brand;

    @Column(length = 60)
    private String model;

    private Integer productionYear;

    private Integer mileageKm;

    protected VehicleAd() { }

    public VehicleAd(String title, String description, long price,
                     User owner, Category category, City city,
                     String brand, String model, Integer productionYear, Integer mileageKm) {
        super(title, description, price, owner, category, city);
        this.brand = brand;
        this.model = model;
        this.productionYear = productionYear;
        this.mileageKm = mileageKm;
    }

    @Override public AdType type() { return AdType.VEHICLE; }
    @Override public String typeLabel() { return "خودرو"; }

    @Override
    public String typeSummary() {
        StringBuilder sb = new StringBuilder();
        if (brand != null) sb.append(brand).append(' ');
        if (model != null) sb.append(model).append(' ');
        if (productionYear != null) sb.append("• مدل ").append(productionYear).append(' ');
        if (mileageKm != null) sb.append("• ").append(mileageKm).append(" کیلومتر");
        return sb.toString().trim();
    }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Integer getProductionYear() { return productionYear; }
    public void setProductionYear(Integer productionYear) { this.productionYear = productionYear; }
    public Integer getMileageKm() { return mileageKm; }
    public void setMileageKm(Integer mileageKm) { this.mileageKm = mileageKm; }
}

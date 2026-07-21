package com.ap.marketplace.domain;

import com.ap.marketplace.domain.enums.AdType;
import jakarta.persistence.*;

/** آگهی عمومی (کالای متفرقه). */
@Entity
@DiscriminatorValue("GENERAL")
public class GeneralAd extends Advertisement {

    /** وضعیت کالا: نو/کارکرده و ... به‌صورت متن آزاد کوتاه. */
    @Column(length = 40)
    private String itemCondition;

    protected GeneralAd() { }

    public GeneralAd(String title, String description, long price,
                     User owner, Category category, City city, String itemCondition) {
        super(title, description, price, owner, category, city);
        this.itemCondition = itemCondition;
    }

    @Override public AdType type() { return AdType.GENERAL; }
    @Override public String typeLabel() { return "عمومی"; }

    @Override
    public String typeSummary() {
        return itemCondition == null ? "" : "وضعیت: " + itemCondition;
    }

    public String getItemCondition() { return itemCondition; }
    public void setItemCondition(String itemCondition) { this.itemCondition = itemCondition; }
}

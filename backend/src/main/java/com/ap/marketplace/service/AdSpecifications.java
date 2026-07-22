package com.ap.marketplace.service;

import com.ap.marketplace.domain.Advertisement;
import com.ap.marketplace.domain.GeneralAd;
import com.ap.marketplace.domain.PropertyAd;
import com.ap.marketplace.domain.VehicleAd;
import com.ap.marketplace.domain.enums.AdStatus;
import com.ap.marketplace.domain.enums.AdType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * ساخت پرس‌وجوی پویا برای فیلترهای ترکیبی (نوع، دسته، شهر، بازه قیمت، متن).
 */
public final class AdSpecifications {

    private AdSpecifications() { }

    public static Specification<Advertisement> build(String query, AdType type, Long categoryId,
                                                     Long cityId, Long minPrice, Long maxPrice,
                                                     AdStatus status) {
        return (root, cq, cb) -> {
            List<Predicate> ps = new ArrayList<>();

            // ۱. فیلتر بر اساس وضعیت آگهی (مثلاً APPROVED)
            if (status != null) {
                ps.add(cb.equal(root.get("status"), status));
            }

            // ۲. فیلتر بر اساس زیرنوع در ارث‌بری
            if (type != null) {
                Class<? extends Advertisement> subtype = switch (type) {
                    case VEHICLE -> VehicleAd.class;
                    case PROPERTY -> PropertyAd.class;
                    case GENERAL -> GeneralAd.class;
                };
                ps.add(cb.equal(root.type(), subtype));
            }

            // ۳. فیلتر بر اساس دسته‌بندی (پشتیبانی از دسته‌بندی مستقیم و دسته‌بندی‌های والد)
            if (categoryId != null) {
                Predicate isDirectCategory = cb.equal(root.get("category").get("id"), categoryId);
                Predicate isParentCategory = cb.equal(root.get("category").get("parent").get("id"), categoryId);

                // آگهی شامل دسته انتخابی یا زیردسته‌های آن باشد
                ps.add(cb.or(isDirectCategory, isParentCategory));
            }

            // ۴. فیلتر بر اساس شهر
            if (cityId != null) {
                ps.add(cb.equal(root.get("city").get("id"), cityId));
            }

            // ۵. فیلتر حداقل قیمت
            if (minPrice != null) {
                ps.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            // ۶. فیلتر حداکثر قیمت
            if (maxPrice != null) {
                ps.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            // ۷. جستجوی متنی در عنوان و توضیحات
            if (query != null && !query.isBlank()) {
                String like = "%" + query.trim().toLowerCase() + "%";
                Predicate inTitle = cb.like(cb.lower(root.get("title")), like);
                Predicate inDesc = cb.like(cb.lower(root.get("description")), like);
                ps.add(cb.or(inTitle, inDesc));
            }

            return cb.and(ps.toArray(new Predicate[0]));
        };
    }
}
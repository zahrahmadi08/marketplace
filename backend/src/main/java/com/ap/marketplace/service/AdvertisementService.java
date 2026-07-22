package com.ap.marketplace.service;

import com.ap.marketplace.domain.*;
import com.ap.marketplace.domain.enums.AdStatus;
import com.ap.marketplace.domain.enums.AdType;
import com.ap.marketplace.dto.ad.*;
import com.ap.marketplace.exception.BadRequestException;
import com.ap.marketplace.exception.ForbiddenException;
import com.ap.marketplace.exception.NotFoundException;
import com.ap.marketplace.repository.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class AdvertisementService {

    private final AdvertisementRepository adRepository;
    private final CategoryRepository categoryRepository;
    private final CityRepository cityRepository;
    private final RatingRepository ratingRepository;

    public AdvertisementService(AdvertisementRepository adRepository, CategoryRepository categoryRepository,
                                CityRepository cityRepository, RatingRepository ratingRepository) {
        this.adRepository = adRepository;
        this.categoryRepository = categoryRepository;
        this.cityRepository = cityRepository;
        this.ratingRepository = ratingRepository;
    }

    /** لیست عمومی: فقط آگهی‌های ACTIVE با فیلتر/جست‌وجو/مرتب‌سازی/صفحه‌بندی. */
    @Transactional(readOnly = true)
    public Page<AdSummaryResponse> search(String query, AdType type, Long categoryId, Long cityId,
                                          Long minPrice, Long maxPrice, String sort, int page, int size) {
        Specification<Advertisement> spec =
                AdSpecifications.build(query, type, categoryId, cityId, minPrice, maxPrice, AdStatus.ACTIVE);
        int safeSize = Math.min(Math.max(size, 1), 50);

        if ("rating".equalsIgnoreCase(sort)) {
            // مرتب‌سازی بر اساس میانگین امتیاز در حافظه، سپس صفحه‌بندی دستی.
            List<Advertisement> all = adRepository.findAll(spec);
            List<AdSummaryResponse> mapped = all.stream()
                    .map(a -> AdSummaryResponse.from(a, ratingRepository.averageForAd(a)))
                    .sorted(Comparator.comparingDouble(AdSummaryResponse::averageRating).reversed())
                    .toList();
            int from = Math.min(page * safeSize, mapped.size());
            int to = Math.min(from + safeSize, mapped.size());
            return new PageImpl<>(mapped.subList(from, to), PageRequest.of(page, safeSize), mapped.size());
        }

        Sort sortSpec = switch (sort == null ? "" : sort) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            default -> Sort.by(Sort.Direction.DESC, "createdAt"); // newest
        };
        Page<Advertisement> p = adRepository.findAll(spec, PageRequest.of(page, safeSize, sortSpec));
        return p.map(a -> AdSummaryResponse.from(a, ratingRepository.averageForAd(a)));
    }

    @Transactional(readOnly = true)
    public AdDetailResponse detail(Long id) {
        Advertisement ad = getVisibleOrThrow(id);
        return AdDetailResponse.from(ad, ratingRepository.averageForAd(ad),
                (int) ratingRepository.countByAdvertisement(ad));
    }

    @Transactional(readOnly = true)
    public List<AdSummaryResponse> mine(User owner) {
        return adRepository.findByOwnerOrderByCreatedAtDesc(owner).stream()
                .map(a -> AdSummaryResponse.from(a, ratingRepository.averageForAd(a)))
                .toList();
    }

    public AdDetailResponse create(User owner, CreateAdRequest req) {
        if (owner.isBlocked()) throw new ForbiddenException("حساب شما مسدود است");
        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new BadRequestException("دسته‌بندی نامعتبر است"));
        City city = cityRepository.findById(req.cityId())
                .orElseThrow(() -> new BadRequestException("شهر نامعتبر است"));

        Advertisement ad = switch (req.type()) {
            case VEHICLE -> {
                var v = req.vehicle();
                if (v == null) throw new BadRequestException("اطلاعات خودرو لازم است");
                yield new VehicleAd(req.title(), req.description(), req.price(), owner, category, city,
                        v.brand(), v.model(), v.productionYear(), v.mileageKm());
            }
            case PROPERTY -> {
                var p = req.property();
                if (p == null) throw new BadRequestException("اطلاعات ملک لازم است");
                yield new PropertyAd(req.title(), req.description(), req.price(), owner, category, city,
                        p.areaSqm(), p.rooms(), p.address(), p.forRent());
            }
            case GENERAL -> {
                var g = req.general();
                yield new GeneralAd(req.title(), req.description(), req.price(), owner, category, city,
                        g == null ? null : g.itemCondition());
            }
        };
        applyImages(ad, req.imageUrls());
        adRepository.save(ad);
        return AdDetailResponse.from(ad, 0, 0);
    }

    public AdDetailResponse update(User actor, Long id, UpdateAdRequest req) {
        Advertisement ad = adRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("آگهی یافت نشد"));
        if (!ad.isOwnedBy(actor)) throw new ForbiddenException("فقط مالک می‌تواند آگهی را ویرایش کند");

        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new BadRequestException("دسته‌بندی نامعتبر است"));
        City city = cityRepository.findById(req.cityId())
                .orElseThrow(() -> new BadRequestException("شهر نامعتبر است"));

        ad.setTitle(req.title());
        ad.setDescription(req.description());
        ad.setPrice(req.price());
        ad.setCategory(category);
        ad.setCity(city);

        if (ad instanceof VehicleAd v && req.vehicle() != null) {
            var f = req.vehicle();
            v.setBrand(f.brand()); v.setModel(f.model());
            v.setProductionYear(f.productionYear()); v.setMileageKm(f.mileageKm());
        } else if (ad instanceof PropertyAd p && req.property() != null) {
            var f = req.property();
            p.setAreaSqm(f.areaSqm()); p.setRooms(f.rooms());
            p.setAddress(f.address()); p.setForRent(f.forRent());
        } else if (ad instanceof GeneralAd g && req.general() != null) {
            g.setItemCondition(req.general().itemCondition());
        }

        if (req.imageUrls() != null) {
            ad.getImages().clear();
            applyImages(ad, req.imageUrls());
        }
        // ویرایش، آگهی را دوباره در صف بررسی قرار می‌دهد.
        ad.setStatus(AdStatus.PENDING);
        return AdDetailResponse.from(ad, ratingRepository.averageForAd(ad),
                (int) ratingRepository.countByAdvertisement(ad));
    }

    public void delete(User actor, Long id) {
        Advertisement ad = adRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("آگهی یافت نشد"));
        if (!ad.isOwnedBy(actor) && !actor.isAdmin()) {
            throw new ForbiddenException("اجازه حذف این آگهی را ندارید");
        }
        ad.setStatus(AdStatus.DELETED); // حذف نرم
    }

    public void markSold(User actor, Long id) {
        Advertisement ad = adRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("آگهی یافت نشد"));
        if (!ad.isOwnedBy(actor)) throw new ForbiddenException("فقط مالک می‌تواند آگهی را فروخته‌شده کند");
        if (ad.getStatus() != AdStatus.ACTIVE) {
            throw new BadRequestException("فقط آگهی فعال قابل علامت‌گذاری فروخته‌شده است");
        }
        ad.setStatus(AdStatus.SOLD);
    }

    /** موجودیت خام برای استفاده سرویس‌های دیگر (چت/امتیاز). */
    @Transactional(readOnly = true)
    public Advertisement entity(Long id) {
        return adRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("آگهی یافت نشد"));
    }

    private Advertisement getVisibleOrThrow(Long id) {
        Advertisement ad = adRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("آگهی یافت نشد"));
        if (ad.getStatus() == AdStatus.DELETED) throw new NotFoundException("آگهی یافت نشد");
        return ad;
    }

    private void applyImages(Advertisement ad, List<String> urls) {
        if (urls == null) return;
        int i = 0;
        for (String url : urls) {
            if (url != null && !url.isBlank()) {
                ad.addImage(new AdImage(url.trim(), i++));
            }
        }
    }
}

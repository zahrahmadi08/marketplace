package com.ap.marketplace.service;

import com.ap.marketplace.domain.Advertisement;
import com.ap.marketplace.domain.Favorite;
import com.ap.marketplace.domain.User;
import com.ap.marketplace.dto.ad.AdSummaryResponse;
import com.ap.marketplace.exception.ConflictException;
import com.ap.marketplace.exception.NotFoundException;
import com.ap.marketplace.repository.FavoriteRepository;
import com.ap.marketplace.repository.RatingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final AdvertisementService advertisementService;
    private final RatingRepository ratingRepository;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           AdvertisementService advertisementService, RatingRepository ratingRepository) {
        this.favoriteRepository = favoriteRepository;
        this.advertisementService = advertisementService;
        this.ratingRepository = ratingRepository;
    }

    @Transactional(readOnly = true)
    public List<AdSummaryResponse> list(User user) {
        return favoriteRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(Favorite::getAdvertisement)
                .map(a -> AdSummaryResponse.from(a, ratingRepository.averageForAd(a)))
                .toList();
    }

    public void add(User user, Long adId) {
        Advertisement ad = advertisementService.entity(adId);
        if (favoriteRepository.existsByUserAndAdvertisement(user, ad)) {
            throw new ConflictException("این آگهی قبلاً در علاقه‌مندی‌ها است");
        }
        favoriteRepository.save(new Favorite(user, ad));
    }

    public void remove(User user, Long adId) {
        Advertisement ad = advertisementService.entity(adId);
        Favorite fav = favoriteRepository.findByUserAndAdvertisement(user, ad)
                .orElseThrow(() -> new NotFoundException("در علاقه‌مندی‌ها یافت نشد"));
        favoriteRepository.delete(fav);
    }
}

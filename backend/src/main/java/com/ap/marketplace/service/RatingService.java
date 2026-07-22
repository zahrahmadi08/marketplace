package com.ap.marketplace.service;

import com.ap.marketplace.domain.Advertisement;
import com.ap.marketplace.domain.Rating;
import com.ap.marketplace.domain.User;
import com.ap.marketplace.dto.rating.RatingRequest;
import com.ap.marketplace.dto.rating.RatingResponse;
import com.ap.marketplace.exception.BadRequestException;
import com.ap.marketplace.exception.ConflictException;
import com.ap.marketplace.repository.RatingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class RatingService {

    private final RatingRepository ratingRepository;
    private final AdvertisementService advertisementService;

    public RatingService(RatingRepository ratingRepository, AdvertisementService advertisementService) {
        this.ratingRepository = ratingRepository;
        this.advertisementService = advertisementService;
    }

    @Transactional(readOnly = true)
    public List<RatingResponse> list(Long adId) {
        Advertisement ad = advertisementService.entity(adId);
        return ratingRepository.findByAdvertisementOrderByCreatedAtDesc(ad).stream()
                .map(RatingResponse::from).toList();
    }

    public RatingResponse rate(User rater, Long adId, RatingRequest req) {
        if (req.score() < 1 || req.score() > 5) {
            throw new BadRequestException("امتیاز باید بین ۱ تا ۵ باشد");
        }
        Advertisement ad = advertisementService.entity(adId);
        if (ad.isOwnedBy(rater)) {
            throw new BadRequestException("نمی‌توانید به آگهی خودتان امتیاز دهید");
        }
        if (ratingRepository.existsByRaterAndAdvertisement(rater, ad)) {
            throw new ConflictException("قبلاً به این آگهی امتیاز داده‌اید");
        }
        Rating rating = ratingRepository.save(new Rating(rater, ad, req.score(), req.comment()));
        return RatingResponse.from(rating);
    }
}

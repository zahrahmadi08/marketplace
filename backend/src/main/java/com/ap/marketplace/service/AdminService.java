package com.ap.marketplace.service;

import com.ap.marketplace.domain.Advertisement;
import com.ap.marketplace.domain.User;
import com.ap.marketplace.domain.enums.AdStatus;
import com.ap.marketplace.domain.enums.UserStatus;
import com.ap.marketplace.dto.ad.AdSummaryResponse;
import com.ap.marketplace.dto.admin.StatsResponse;
import com.ap.marketplace.dto.auth.UserResponse;
import com.ap.marketplace.exception.BadRequestException;
import com.ap.marketplace.exception.NotFoundException;
import com.ap.marketplace.repository.AdvertisementRepository;
import com.ap.marketplace.repository.RatingRepository;
import com.ap.marketplace.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class AdminService {

    private final AdvertisementRepository adRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;

    public AdminService(AdvertisementRepository adRepository, UserRepository userRepository,
                        RatingRepository ratingRepository) {
        this.adRepository = adRepository;
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
    }

    @Transactional(readOnly = true)
    public List<AdSummaryResponse> pendingAds() {
        return adRepository.findByStatusOrderByCreatedAtAsc(AdStatus.PENDING).stream()
                .map(a -> AdSummaryResponse.from(a, ratingRepository.averageForAd(a)))
                .toList();
    }

    public void approve(Long adId) {
        Advertisement ad = requirePending(adId);
        ad.setStatus(AdStatus.ACTIVE);
    }

    public void reject(Long adId) {
        Advertisement ad = requirePending(adId);
        ad.setStatus(AdStatus.REJECTED);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> users() {
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }

    public void block(Long userId) { setStatus(userId, UserStatus.BLOCKED); }
    public void unblock(Long userId) { setStatus(userId, UserStatus.ACTIVE); }

    @Transactional(readOnly = true)
    public StatsResponse stats() {
        return new StatsResponse(
                userRepository.count(),
                adRepository.count(),
                adRepository.countByStatus(AdStatus.PENDING),
                adRepository.countByStatus(AdStatus.ACTIVE));
    }

    private Advertisement requirePending(Long adId) {
        Advertisement ad = adRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("آگهی یافت نشد"));
        if (ad.getStatus() != AdStatus.PENDING) {
            throw new BadRequestException("فقط آگهی در انتظار بررسی قابل تأیید/رد است");
        }
        return ad;
    }

    private void setStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("کاربر یافت نشد"));
        if (user.isAdmin()) throw new BadRequestException("نمی‌توان وضعیت ادمین را تغییر داد");
        user.setStatus(status);
    }
}

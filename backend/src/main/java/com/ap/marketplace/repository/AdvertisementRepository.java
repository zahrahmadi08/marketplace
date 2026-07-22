package com.ap.marketplace.repository;

import com.ap.marketplace.domain.Advertisement;
import com.ap.marketplace.domain.User;
import com.ap.marketplace.domain.enums.AdStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

/** JpaSpecificationExecutor برای جست‌وجوی ترکیبی چندفیلتره . */
public interface AdvertisementRepository
        extends JpaRepository<Advertisement, Long>, JpaSpecificationExecutor<Advertisement> {

    List<Advertisement> findByOwnerOrderByCreatedAtDesc(User owner);
    List<Advertisement> findByStatusOrderByCreatedAtAsc(AdStatus status);
    long countByStatus(AdStatus status);
}

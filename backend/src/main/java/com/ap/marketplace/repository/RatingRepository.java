package com.ap.marketplace.repository;

import com.ap.marketplace.domain.Advertisement;
import com.ap.marketplace.domain.Rating;
import com.ap.marketplace.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByAdvertisementOrderByCreatedAtDesc(Advertisement advertisement);
    Optional<Rating> findByRaterAndAdvertisement(User rater, Advertisement advertisement);
    boolean existsByRaterAndAdvertisement(User rater, Advertisement advertisement);

    @Query("select coalesce(avg(r.score), 0) from Rating r where r.advertisement = :ad")
    double averageForAd(Advertisement ad);

    long countByAdvertisement(Advertisement ad);
}

package com.ap.marketplace.repository;

import com.ap.marketplace.domain.Advertisement;
import com.ap.marketplace.domain.Favorite;
import com.ap.marketplace.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserOrderByCreatedAtDesc(User user);
    Optional<Favorite> findByUserAndAdvertisement(User user, Advertisement advertisement);
    boolean existsByUserAndAdvertisement(User user, Advertisement advertisement);
}

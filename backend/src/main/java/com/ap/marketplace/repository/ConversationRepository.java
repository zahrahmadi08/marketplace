package com.ap.marketplace.repository;

import com.ap.marketplace.domain.Advertisement;
import com.ap.marketplace.domain.Conversation;
import com.ap.marketplace.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByAdvertisementAndBuyer(Advertisement advertisement, User buyer);

    @Query("select c from Conversation c where c.buyer = :user or c.seller = :user order by c.createdAt desc")
    List<Conversation> findAllForUser(User user);
}

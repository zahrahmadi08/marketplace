package com.ap.marketplace;

import com.ap.marketplace.domain.*;
import com.ap.marketplace.dto.rating.RatingRequest;
import com.ap.marketplace.exception.BadRequestException;
import com.ap.marketplace.exception.ConflictException;
import com.ap.marketplace.repository.RatingRepository;
import com.ap.marketplace.service.AdvertisementService;
import com.ap.marketplace.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock RatingRepository ratingRepository;
    @Mock AdvertisementService advertisementService;
    @InjectMocks RatingService ratingService;

    private User owner;
    private User rater;
    private Advertisement ad;

    @BeforeEach
    void setup() throws Exception {
        owner = new User("owner", "h", "Owner", "o@e.com", "09120000001");
        rater = new User("rater", "h", "Rater", "r@e.com", "09120000002");
        setId(owner, 1L);
        setId(rater, 2L);
        ad = new GeneralAd("t", "d", 100L, owner, new Category("c", null), new City("Tehran"), "used");
    }

    @Test
    void rejectsScoreOutOfRange() {
        // بررسی بازه پیش از واکشی آگهی رخ می‌دهد؛ نیازی به stub نیست.
        assertThrows(BadRequestException.class,
                () -> ratingService.rate(rater, 10L, new RatingRequest(6, null)));
    }

    @Test
    void rejectsRatingOwnAd() {
        when(advertisementService.entity(10L)).thenReturn(ad);
        assertThrows(BadRequestException.class,
                () -> ratingService.rate(owner, 10L, new RatingRequest(5, "nice")));
    }

    @Test
    void rejectsDuplicateRating() {
        when(advertisementService.entity(10L)).thenReturn(ad);
        when(ratingRepository.existsByRaterAndAdvertisement(rater, ad)).thenReturn(true);
        assertThrows(ConflictException.class,
                () -> ratingService.rate(rater, 10L, new RatingRequest(4, null)));
    }

    @Test
    void acceptsValidRating() {
        when(advertisementService.entity(10L)).thenReturn(ad);
        when(ratingRepository.existsByRaterAndAdvertisement(rater, ad)).thenReturn(false);
        when(ratingRepository.save(any(Rating.class))).thenAnswer(i -> i.getArgument(0));

        var response = ratingService.rate(rater, 10L, new RatingRequest(4, "خوب بود"));

        assertEquals(4, response.score());
        assertEquals("خوب بود", response.comment());
        verify(ratingRepository).save(any(Rating.class));
    }

    // کمک‌کننده: مقداردهی id از طریق reflection (چون setter ندارد)
    private static void setId(Object entity, Long id) throws Exception {
        Field f = entity.getClass().getDeclaredField("id");
        f.setAccessible(true);
        f.set(entity, id);
    }
}

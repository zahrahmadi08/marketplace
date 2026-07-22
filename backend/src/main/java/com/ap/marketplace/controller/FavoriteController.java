package com.ap.marketplace.controller;

import com.ap.marketplace.dto.ad.AdSummaryResponse;
import com.ap.marketplace.security.CurrentUserProvider;
import com.ap.marketplace.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final CurrentUserProvider currentUser;

    public FavoriteController(FavoriteService favoriteService, CurrentUserProvider currentUser) {
        this.favoriteService = favoriteService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public List<AdSummaryResponse> list() {
        return favoriteService.list(currentUser.require());
    }

    @PostMapping("/{adId}")
    public ResponseEntity<Void> add(@PathVariable Long adId) {
        favoriteService.add(currentUser.require(), adId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{adId}")
    public ResponseEntity<Void> remove(@PathVariable Long adId) {
        favoriteService.remove(currentUser.require(), adId);
        return ResponseEntity.noContent().build();
    }
}

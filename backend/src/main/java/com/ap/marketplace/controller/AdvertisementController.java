package com.ap.marketplace.controller;

import com.ap.marketplace.domain.enums.AdType;
import com.ap.marketplace.dto.ad.*;
import com.ap.marketplace.security.CurrentUserProvider;
import com.ap.marketplace.service.AdvertisementService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ads")
public class AdvertisementController {

    private final AdvertisementService adService;
    private final CurrentUserProvider currentUser;

    public AdvertisementController(AdvertisementService adService, CurrentUserProvider currentUser) {
        this.adService = adService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public Page<AdSummaryResponse> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) AdType type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long cityId,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false, defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return adService.search(query, type, categoryId, cityId, minPrice, maxPrice, sort, page, size);
    }

    @GetMapping("/mine")
    public List<AdSummaryResponse> mine() {
        return adService.mine(currentUser.require());
    }

    @GetMapping("/{id}")
    public AdDetailResponse detail(@PathVariable Long id) {
        return adService.detail(id);
    }

    @PostMapping
    public ResponseEntity<AdDetailResponse> create(@Valid @RequestBody CreateAdRequest req) {
        AdDetailResponse created = adService.create(currentUser.require(), req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public AdDetailResponse update(@PathVariable Long id, @Valid @RequestBody UpdateAdRequest req) {
        return adService.update(currentUser.require(), id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adService.delete(currentUser.require(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sold")
    public ResponseEntity<Void> markSold(@PathVariable Long id) {
        adService.markSold(currentUser.require(), id);
        return ResponseEntity.noContent().build();
    }
}

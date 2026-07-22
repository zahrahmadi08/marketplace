package com.ap.marketplace.controller;

import com.ap.marketplace.dto.lookup.CategoryResponse;
import com.ap.marketplace.dto.lookup.CityResponse;
import com.ap.marketplace.service.LookupService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class LookupController {

    private final LookupService lookupService;

    public LookupController(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    @GetMapping("/api/categories")
    public List<CategoryResponse> categories() {
        return lookupService.categories();
    }

    @GetMapping("/api/cities")
    public List<CityResponse> cities() {
        return lookupService.cities();
    }
}

package com.ap.marketplace.service;

import com.ap.marketplace.dto.lookup.CategoryResponse;
import com.ap.marketplace.dto.lookup.CityResponse;
import com.ap.marketplace.repository.CategoryRepository;
import com.ap.marketplace.repository.CityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LookupService {

    private final CategoryRepository categoryRepository;
    private final CityRepository cityRepository;

    public LookupService(CategoryRepository categoryRepository, CityRepository cityRepository) {
        this.categoryRepository = categoryRepository;
        this.cityRepository = cityRepository;
    }

    public List<CategoryResponse> categories() {
        return categoryRepository.findAll().stream().map(CategoryResponse::from).toList();
    }

    public List<CityResponse> cities() {
        return cityRepository.findAll().stream().map(CityResponse::from).toList();
    }
}

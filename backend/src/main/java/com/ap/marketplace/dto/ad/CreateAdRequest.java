package com.ap.marketplace.dto.ad;

import com.ap.marketplace.domain.enums.AdType;
import jakarta.validation.constraints.*;
import java.util.List;

public record CreateAdRequest(
        @NotNull AdType type,
        @NotBlank @Size(max = 150) String title,
        @NotBlank @Size(max = 4000) String description,
        @PositiveOrZero long price,
        @NotNull Long categoryId,
        @NotNull Long cityId,
        List<String> imageUrls,
        VehicleFields vehicle,
        PropertyFields property,
        GeneralFields general
) {
    public record VehicleFields(String brand, String model, Integer productionYear, Integer mileageKm) { }
    public record PropertyFields(Integer areaSqm, Integer rooms, String address, boolean forRent) { }
    public record GeneralFields(String itemCondition) { }
}

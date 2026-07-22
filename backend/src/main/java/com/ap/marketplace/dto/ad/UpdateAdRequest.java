package com.ap.marketplace.dto.ad;

import jakarta.validation.constraints.*;
import java.util.List;

/** ویرایش آگهی؛ نوع آگهی تغییر نمی‌کند. گروه فیلد متناسب با نوع موجود اعمال می‌شود. */
public record UpdateAdRequest(
        @NotBlank @Size(max = 150) String title,
        @NotBlank @Size(max = 4000) String description,
        @PositiveOrZero long price,
        @NotNull Long categoryId,
        @NotNull Long cityId,
        List<String> imageUrls,
        CreateAdRequest.VehicleFields vehicle,
        CreateAdRequest.PropertyFields property,
        CreateAdRequest.GeneralFields general
) { }

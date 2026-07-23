package com.ap.marketplace.client.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/** پوشش صفحه‌بندی مطابق خروجی Spring Page. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PageDto<T>(List<T> content, int totalElements, int totalPages, int number, int size) { }

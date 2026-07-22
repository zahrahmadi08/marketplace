package com.ap.marketplace.dto.lookup;

import com.ap.marketplace.domain.Category;

public record CategoryResponse(Long id, String name, Long parentId) {
    public static CategoryResponse from(Category c) {
        Long parentId = c.getParent() == null ? null : c.getParent().getId();
        return new CategoryResponse(c.getId(), c.getName(), parentId);
    }
}

package com.ap.marketplace.dto.admin;

public record StatsResponse(long userCount, long adCount, long pendingCount, long activeCount) { }

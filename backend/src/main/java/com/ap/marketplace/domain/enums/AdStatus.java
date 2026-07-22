package com.ap.marketplace.domain.enums;

/**
 * چرخه حیات آگهی.
 * PENDING: در انتظار بررسی ادمین. ACTIVE: تأییدشده و قابل نمایش عمومی.
 * REJECTED: ردشده. SOLD: فروخته‌شده. DELETED: حذف نرم توسط مالک/ادمین.
 */
public enum AdStatus {
    PENDING,
    ACTIVE,
    REJECTED,
    SOLD,
    DELETED
}

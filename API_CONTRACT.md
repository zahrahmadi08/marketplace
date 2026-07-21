# قرارداد API — سامانه آگهی دست دوم

پایه: `http://localhost:8080` — همه‌ی پاسخ‌ها JSON. احراز هویت با هدر `Authorization: Bearer <token>`.
همه‌ی مسیرها جز `/api/auth/**`، `GET /api/ads`, `GET /api/ads/{id}`, `GET /api/categories`, `GET /api/cities` نیازمند توکن‌اند.

## قالب خطای یکسان
همه خطاها با این ساختار برمی‌گردند (هندلر سراسری):
```json
{ "timestamp": "2026-07-06T12:00:00Z", "status": 400, "error": "Bad Request", "message": "متن قابل‌فهم", "path": "/api/ads" }
```

---

## احراز هویت
| متد | مسیر | بدنه | پاسخ |
|---|---|---|---|
| POST | `/api/auth/register` | `RegisterRequest` | `AuthResponse` (توکن + کاربر) |
| POST | `/api/auth/login` | `LoginRequest` | `AuthResponse` |
| GET  | `/api/auth/me` | — | `UserResponse` |

`RegisterRequest`: `{ username, password, fullName, email?, phone }`
`LoginRequest`: `{ username, password }`
`AuthResponse`: `{ token, user: UserResponse }`
`UserResponse`: `{ id, username, fullName, email, phone, role, status }`

## آگهی‌ها
| متد | مسیر | توضیح |
|---|---|---|
| GET | `/api/ads` | لیست عمومی ACTIVE با فیلتر/جست‌وجو/مرتب‌سازی/صفحه‌بندی |
| GET | `/api/ads/{id}` | جزئیات یک آگهی |
| GET | `/api/ads/mine` | آگهی‌های کاربر جاری (هر وضعیتی) |
| POST | `/api/ads` | ثبت آگهی جدید (وضعیت اولیه PENDING) |
| PUT | `/api/ads/{id}` | ویرایش (فقط مالک) |
| DELETE | `/api/ads/{id}` | حذف نرم (مالک یا ادمین) → DELETED |
| POST | `/api/ads/{id}/sold` | علامت‌گذاری فروخته‌شده (فقط مالک، فقط از ACTIVE) |

پارامترهای `GET /api/ads`: `query, type(VEHICLE|PROPERTY|GENERAL), categoryId, cityId, minPrice, maxPrice, sort(newest|price_asc|price_desc|rating), page, size`.
`CreateAdRequest`: `{ type, title, description, price, categoryId, cityId, imageUrls[], vehicle?{brand,model,productionYear,mileageKm}, property?{areaSqm,rooms,address,forRent}, general?{itemCondition} }`
`AdSummaryResponse`: `{ id, type, typeLabel, title, price, cityName, categoryName, thumbnailUrl, typeSummary, averageRating, status }`
`AdDetailResponse`: خلاصه + `{ description, owner:UserResponse, imageUrls[], createdAt, ratingCount, typeDetails }`

## علاقه‌مندی
| متد | مسیر |
|---|---|
| GET | `/api/favorites` |
| POST | `/api/favorites/{adId}` |
| DELETE | `/api/favorites/{adId}` |

## چت
| متد | مسیر | توضیح |
|---|---|---|
| GET | `/api/conversations` | لیست گفت‌وگوهای کاربر (+ آخرین پیام، تعداد نادیده) |
| POST | `/api/conversations` | شروع گفت‌وگو `{ adId }` → `ConversationResponse` |
| GET | `/api/conversations/{id}/messages` | پیام‌ها (طرف مقابل را seen می‌کند) |
| POST | `/api/conversations/{id}/messages` | ارسال پیام `{ text }` |

## امتیازدهی
| متد | مسیر | توضیح |
|---|---|---|
| POST | `/api/ads/{id}/ratings` | `{ score(1..5), comment? }` |
| GET | `/api/ads/{id}/ratings` | لیست امتیازها |

## داده‌های کمکی
| متد | مسیر |
|---|---|
| GET | `/api/categories` | درخت دسته‌ها |
| GET | `/api/cities` | فهرست شهرها |

## ادمین (نقش ADMIN)
| متد | مسیر | توضیح |
|---|---|---|
| GET | `/api/admin/ads/pending` | آگهی‌های در انتظار بررسی |
| POST | `/api/admin/ads/{id}/approve` | تأیید → ACTIVE |
| POST | `/api/admin/ads/{id}/reject` | رد → REJECTED |
| GET | `/api/admin/users` | فهرست کاربران |
| POST | `/api/admin/users/{id}/block` | مسدودسازی |
| POST | `/api/admin/users/{id}/unblock` | رفع مسدودی |
| GET | `/api/admin/stats` | `{ userCount, adCount, pendingCount, activeCount }` |

## کدهای وضعیت
`200` موفق · `201` ایجاد · `400` ورودی نامعتبر · `401` بدون/نامعتبر توکن · `403` غیرمجاز (مالکیت/نقش) · `404` یافت نشد · `409` تعارض (تکراری).

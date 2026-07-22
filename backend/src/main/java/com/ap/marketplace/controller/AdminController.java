package com.ap.marketplace.controller;

import com.ap.marketplace.dto.ad.AdSummaryResponse;
import com.ap.marketplace.dto.admin.StatsResponse;
import com.ap.marketplace.dto.auth.UserResponse;
import com.ap.marketplace.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** همه‌ی مسیرها با نقش ADMIN محافظت شده‌اند (در SecurityConfig). */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/ads/pending")
    public List<AdSummaryResponse> pending() {
        return adminService.pendingAds();
    }

    @PostMapping("/ads/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable Long id) {
        adminService.approve(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/ads/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable Long id) {
        adminService.reject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public List<UserResponse> users() {
        return adminService.users();
    }

    @PostMapping("/users/{id}/block")
    public ResponseEntity<Void> block(@PathVariable Long id) {
        adminService.block(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{id}/unblock")
    public ResponseEntity<Void> unblock(@PathVariable Long id) {
        adminService.unblock(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public StatsResponse stats() {
        return adminService.stats();
    }
}

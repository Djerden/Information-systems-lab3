package com.djeno.backend_lab1.controllers;

import com.djeno.backend_lab1.models.AdminRequest;
import com.djeno.backend_lab1.service.AdminRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminRequestController {
    private final AdminRequestService adminRequestService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyForAdminRole() {
        System.out.println("Обращение в точку apply (заявка на админа)");
        adminRequestService.createAdminRequest();
        return ResponseEntity.ok("Your request has been submitted");
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminRequest>> getAllRequests() {
        return ResponseEntity.ok(adminRequestService.getAllRequests());
    }

    @PatchMapping("/{requestId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveRequest(@PathVariable Long requestId) {
        adminRequestService.approveRequest(requestId);
        return ResponseEntity.ok("Request approved");
    }

    @PatchMapping("/{requestId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectRequest(@PathVariable Long requestId) {
        adminRequestService.rejectRequest(requestId);
        return ResponseEntity.ok("Request rejected");
    }
}

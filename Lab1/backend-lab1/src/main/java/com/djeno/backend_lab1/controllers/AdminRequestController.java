package com.djeno.backend_lab1.controllers;

import com.djeno.backend_lab1.models.AdminRequest;
import com.djeno.backend_lab1.models.enums.AdminRequestStatus;
import com.djeno.backend_lab1.service.AdminRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        adminRequestService.createAdminRequest();
        return ResponseEntity.ok("Your request has been submitted");
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AdminRequest>> getAllRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return ResponseEntity.ok(adminRequestService.getAllRequests(pageable));
    }

    // Получить заявки по статусу с пагинацией и сортировкой
    @GetMapping("/filter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AdminRequest>> getRequestsByStatus(
            @RequestParam AdminRequestStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return ResponseEntity.ok(adminRequestService.getRequestsByStatus(status, pageable));
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

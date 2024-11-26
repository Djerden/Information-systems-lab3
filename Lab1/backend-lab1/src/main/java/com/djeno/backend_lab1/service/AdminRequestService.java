package com.djeno.backend_lab1.service;

import com.djeno.backend_lab1.models.AdminRequest;
import com.djeno.backend_lab1.models.enums.AdminRequestStatus;
import com.djeno.backend_lab1.models.enums.Role;
import com.djeno.backend_lab1.repositories.AdminRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminRequestService {
    private final UserService userService;
    private final AdminRequestRepository adminRequestRepository;

    // Получить все заявки с пагинацией и сортировкой
    public Page<AdminRequest> getAllRequests(Pageable pageable) {
        return adminRequestRepository.findAll(pageable);
    }

    // Получить заявки по статусу с пагинацией и сортировкой
    public Page<AdminRequest> getRequestsByStatus(AdminRequestStatus status, Pageable pageable) {
        return adminRequestRepository.findByStatus(status, pageable);
    }

    // Создание заявки
    public void createAdminRequest() {
        var currentUser = userService.getCurrentUser();

        // Проверяем, есть ли уже администратор в системе
        boolean hasAdmin = userService.existsByRole(Role.ROLE_ADMIN);

        if (!hasAdmin) {
            // Если администратора нет, сразу назначаем роль ADMIN
            currentUser.setRole(Role.ROLE_ADMIN);
            userService.save(currentUser);
        } else {
            // Если администратор есть, создаем заявку со статусом PENDING
            AdminRequest request = AdminRequest.builder()
                    .user(currentUser)
                    .status(AdminRequestStatus.PENDING)
                    .build();
            adminRequestRepository.save(request);
        }
    }

    // Одобрение заявки
    public void approveRequest(Long requestId) {
        AdminRequest request = adminRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(AdminRequestStatus.APPROVED);
        adminRequestRepository.save(request);

        // Назначаем пользователю роль ADMIN
        var user = request.getUser();
        user.setRole(Role.ROLE_ADMIN);
        userService.save(user);
    }

    // Отклонение заявки
    public void rejectRequest(Long requestId) {
        AdminRequest request = adminRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(AdminRequestStatus.REJECTED);
        adminRequestRepository.save(request);
    }
}


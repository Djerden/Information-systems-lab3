package com.djeno.backend_lab1.repositories;

import com.djeno.backend_lab1.models.AdminRequest;
import com.djeno.backend_lab1.models.enums.AdminRequestStatus;
import com.djeno.backend_lab1.models.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRequestRepository extends JpaRepository<AdminRequest, Long> {
    // Получить заявки с определённым статусом
    Page<AdminRequest> findByStatus(AdminRequestStatus status, Pageable pageable);

    // Получить все заявки с пагинацией
    Page<AdminRequest> findAll(Pageable pageable);
}

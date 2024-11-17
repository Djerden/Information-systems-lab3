package com.djeno.backend_lab1.repositories;

import com.djeno.backend_lab1.models.AdminRequest;
import com.djeno.backend_lab1.models.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRequestRepository extends JpaRepository<AdminRequest, Long> {
}

package com.djeno.backend_lab1.DTO;

import com.djeno.backend_lab1.models.enums.AdminRequestStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminRequestResponseDTO {
    private Long id;
    private UserDTO user; // DTO вместо сущности User
    private AdminRequestStatus status;
    private LocalDateTime createdAt;
}

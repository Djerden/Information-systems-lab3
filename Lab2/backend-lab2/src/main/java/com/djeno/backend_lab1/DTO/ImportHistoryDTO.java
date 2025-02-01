package com.djeno.backend_lab1.DTO;

import com.djeno.backend_lab1.models.enums.ImportStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ImportHistoryDTO {

    private Long id;

    private UserDTO user; // DTO для пользователя

    private ImportStatus status; // Статус операции

    private LocalDateTime timestamp; // Время операции

    private int addedObjects; // Число добавленных объектов
}

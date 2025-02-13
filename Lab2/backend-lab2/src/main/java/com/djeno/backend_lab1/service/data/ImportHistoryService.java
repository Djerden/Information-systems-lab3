package com.djeno.backend_lab1.service.data;

import com.djeno.backend_lab1.DTO.ImportHistoryDTO;
import com.djeno.backend_lab1.DTO.UserDTO;
import com.djeno.backend_lab1.exceptions.AccessDeniedException;
import com.djeno.backend_lab1.models.ImportHistory;
import com.djeno.backend_lab1.models.User;
import com.djeno.backend_lab1.models.enums.ImportStatus;
import com.djeno.backend_lab1.models.enums.Role;
import com.djeno.backend_lab1.repositories.ImportHistoryRepository;
import com.djeno.backend_lab1.service.UserService;
import com.djeno.backend_lab1.service.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportHistoryService {
    private final ImportHistoryRepository importHistoryRepository;

    private final UserService userService;

    private final WebSocketNotificationService webSocketNotificationService;

    // Сохранится в любом случае, даже если транзакция по записи данных откатится
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ImportHistory saveImportHistory(ImportHistory importHistory) {
        webSocketNotificationService.sendNotification("import-history", "saved");
        return importHistoryRepository.save(importHistory);
    }

    public Page<ImportHistoryDTO> getHistoryByUser(int page, int size, String sortBy, String direction, String status) {
        User currentUser = userService.getCurrentUser(); // Получаем текущего пользователя

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));

        Page<ImportHistory> history;
        if (status != null) {
            // Фильтрация по статусу
            history = importHistoryRepository.findByUserAndStatus(currentUser, ImportStatus.valueOf(status), pageable);
        } else {
            history = importHistoryRepository.findByUser(currentUser, pageable);
        }

        return convertToDTOPage(history, pageable); // Преобразуем результат в DTO
    }

    public Page<ImportHistoryDTO> getAllHistory(int page, int size, String sortBy, String direction, String status) {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new AccessDeniedException("Access Denied");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));

        Page<ImportHistory> history;
        if (status != null) {
            // Фильтрация по статусу для админа
            history = importHistoryRepository.findByStatus(ImportStatus.valueOf(status), pageable);
        } else {
            history = importHistoryRepository.findAll(pageable);
        }

        return convertToDTOPage(history, pageable); // Преобразуем результат в DTO
    }

    // Преобразование ImportHistory в ImportHistoryDTO
    private ImportHistoryDTO toDTO(ImportHistory importHistory) {
        ImportHistoryDTO dto = new ImportHistoryDTO();
        dto.setId(importHistory.getId());
        // Проверка на null перед преобразованием пользователя
        if (importHistory.getUser() != null) {
            dto.setUser(new UserDTO(importHistory.getUser().getId(), importHistory.getUser().getUsername()));
        } else {
            dto.setUser(null); // Можно установить null, если пользователь не найден
        }
        dto.setStatus(importHistory.getStatus());
        dto.setTimestamp(importHistory.getTimestamp());
        dto.setAddedObjects(importHistory.getAddedObjects());
        return dto;
    }

    // Преобразование Page<ImportHistory> в Page<ImportHistoryDTO>
    private Page<ImportHistoryDTO> convertToDTOPage(Page<ImportHistory> importHistoryPage, Pageable pageable) {
        List<ImportHistoryDTO> dtoList = importHistoryPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, importHistoryPage.getTotalElements());
    }
}

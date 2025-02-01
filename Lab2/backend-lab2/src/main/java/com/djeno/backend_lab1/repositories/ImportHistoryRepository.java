package com.djeno.backend_lab1.repositories;

import com.djeno.backend_lab1.models.ImportHistory;
import com.djeno.backend_lab1.models.User;
import com.djeno.backend_lab1.models.enums.ImportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImportHistoryRepository extends JpaRepository<ImportHistory, Long> {
    List<ImportHistory> findByUser(User user);

    Page<ImportHistory> findByUser(User user, Pageable pageable);

    Page<ImportHistory> findAll(Pageable pageable);

    // Метод для поиска по пользователю и статусу с пагинацией
    Page<ImportHistory> findByUserAndStatus(User user, ImportStatus status, Pageable pageable);

    // Метод для поиска всех записей по статусу с пагинацией (для админа)
    Page<ImportHistory> findByStatus(ImportStatus status, Pageable pageable);
}

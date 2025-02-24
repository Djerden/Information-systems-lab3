package com.djeno.backend_lab1.models;

import com.djeno.backend_lab1.models.enums.ImportStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "import_history")
public class ImportHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ImportStatus status; // Статус операции (PROCESSING, SUCCESS, FAILED)

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp; // Время операции

    @Column(name = "added_objects", nullable = false)
    private int addedObjects; // Число добавленных объектов

    @Column(name = "file_name", nullable = false)
    private String fileName;

    // Здесь будет храниться уникальный идентификатор файла UUID
    @Column(name = "file_url")
    private String fileUrl;
}

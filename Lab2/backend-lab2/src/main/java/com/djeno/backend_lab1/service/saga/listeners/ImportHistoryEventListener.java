package com.djeno.backend_lab1.service.saga.listeners;

import com.djeno.backend_lab1.models.ImportHistory;
import com.djeno.backend_lab1.models.User;
import com.djeno.backend_lab1.models.enums.ImportStatus;
import com.djeno.backend_lab1.service.UserService;
import com.djeno.backend_lab1.service.data.ImportHistoryService;
import com.djeno.backend_lab1.service.saga.events.DataUploadedEvent;
import com.djeno.backend_lab1.service.saga.events.HistoryRecordCreatedEvent;
import com.djeno.backend_lab1.service.saga.events.TransactionFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ImportHistoryEventListener {

    private final UserService userService;
    private final ImportHistoryService importHistoryService;
    private final ApplicationEventPublisher eventPublisher;


    // Метод с которого начинается вся логика Саги
    public void createRecordImportHistory(MultipartFile file) {
        User currentUser = userService.getCurrentUser();
        String filename = file.getOriginalFilename();

        ImportHistory importHistoryRecord = ImportHistory.builder()
                .user(currentUser)
                .status(ImportStatus.PROCESSING)
                .timestamp(LocalDateTime.now())
                .addedObjects(0)
                .fileName(filename)
                .fileUrl(null)
                .build();

        importHistoryRecord = importHistoryService.saveImportHistory(importHistoryRecord);
        System.out.println("Запись в истории импорта создана");

        eventPublisher.publishEvent(new HistoryRecordCreatedEvent(file, importHistoryRecord));
    }

    @EventListener
    public void handleDataUploadedEvent(DataUploadedEvent event) {
        ImportHistory importHistoryRecord = event.getImportHistoryRecord();
        importHistoryRecord.setStatus(ImportStatus.SUCCESS);
        try {
            importHistoryService.saveImportHistory(importHistoryRecord);
        } catch (Exception e) {
            System.out.println("Не удалось сохранить importHistoryRecord SUCCESS");
            return;
        }
        System.out.println("Данные записи в истории импорта обновлены SUCCESS");
    }

    @EventListener
    public void handleTransactionFailedEvent(TransactionFailedEvent event) {
        ImportHistory importHistoryRecord = event.getImportHistoryRecord();
        importHistoryRecord.setStatus(ImportStatus.FAILED);
        importHistoryRecord.setAddedObjects(0);
        importHistoryRecord.setFileUrl(null);
        try {
            importHistoryService.saveImportHistory(importHistoryRecord);
        } catch (Exception e) {
            System.out.println("Не удалось сохранить importHistoryRecord FAILED");
            return;
        }
        System.out.println("Данные записи в истории импорта обновлены FAILED");
    }
}

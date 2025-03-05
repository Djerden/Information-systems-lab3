package com.djeno.backend_lab1.service.saga.listeners;

import com.djeno.backend_lab1.DTO.DataContainer;
import com.djeno.backend_lab1.models.ImportHistory;
import com.djeno.backend_lab1.service.ImportService;
import com.djeno.backend_lab1.service.MinioService;
import com.djeno.backend_lab1.service.saga.events.DataUploadedEvent;
import com.djeno.backend_lab1.service.saga.events.FileUploadedEvent;
import com.djeno.backend_lab1.service.saga.events.HistoryRecordCreatedEvent;
import com.djeno.backend_lab1.service.saga.events.TransactionFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostgresEventListener {

    private final ImportService importService;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    public void handleFileUploadedEvent(FileUploadedEvent event) {
        ImportHistory importHistoryRecord = event.getImportHistoryRecord();

        try {
            int addedObjects = importService.importYamlData(event.getFile());
            importHistoryRecord.setAddedObjects(addedObjects);
            System.out.println("Postgres: данные сохранены");
        } catch (Exception e) {
            System.out.println("Postgres: ошибка при записи данных: " + e.getMessage() + " Откат...");
            eventPublisher.publishEvent(new TransactionFailedEvent(event.getFileUrl(), importHistoryRecord));
            return;
        }

        eventPublisher.publishEvent(new DataUploadedEvent(importHistoryRecord));
    }
}

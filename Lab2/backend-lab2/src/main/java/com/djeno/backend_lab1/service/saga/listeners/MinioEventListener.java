package com.djeno.backend_lab1.service.saga.listeners;

import com.djeno.backend_lab1.models.ImportHistory;
import com.djeno.backend_lab1.service.MinioService;
import com.djeno.backend_lab1.service.saga.events.FileUploadedEvent;
import com.djeno.backend_lab1.service.saga.events.HistoryRecordCreatedEvent;
import com.djeno.backend_lab1.service.saga.events.TransactionFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MinioEventListener {

    private final MinioService minioService;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    public void handleHistoryCreatedEvent(HistoryRecordCreatedEvent event) {
        ImportHistory importHistoryRecord = event.getImportHistoryRecord();
        String fileUrl = null;

        try {
            System.out.println("Minio: 4 сек сна на ручное прерывание");
            Thread.sleep(4* 1000);
        } catch (Exception e) {
            System.out.println("Поток был прерван: " + e.getMessage());
        }

        try {
            fileUrl = minioService.uploadFile(event.getFile(), MinioService.IMPORTED_FILES);
            importHistoryRecord.setFileUrl(fileUrl);
            System.out.println("Minio: файл" + fileUrl + " загружен");
        } catch (Exception e) {
            System.out.println("Minio: ошибка при загрузке файла: " + e.getMessage() + " Откат...");
            eventPublisher.publishEvent(new TransactionFailedEvent(fileUrl, importHistoryRecord));
            return;
        }
        eventPublisher.publishEvent(new FileUploadedEvent(event.getFile(), fileUrl, importHistoryRecord));
    }

    @EventListener
    public void handleTransactionFailedEvent(TransactionFailedEvent event) {
        ImportHistory importHistoryRecord = event.getImportHistoryRecord();

        // Проверяем, что fileUrl не равен null
        if (event.getFileUrl() == null) {
            System.out.println("Minio: файл не был загружен, удаление не требуется");
            return;
        }

        try {
            minioService.deleteFile(event.getFileUrl(), MinioService.IMPORTED_FILES);
        } catch (Exception e) {
            System.out.println("Minio: ошибка при попытке удаления файла: " + e.getMessage());
            return;
        }
        System.out.println("Minio: изменения откачены");
    }
}

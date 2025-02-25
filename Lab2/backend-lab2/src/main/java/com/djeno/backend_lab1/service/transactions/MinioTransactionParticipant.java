package com.djeno.backend_lab1.service.transactions;

import com.djeno.backend_lab1.models.ImportHistory;
import com.djeno.backend_lab1.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

//@Scope("prototype") // или заменить на request
//@Component
public class MinioTransactionParticipant implements TransactionParticipant {
    private static final String BUCKET_NAME = "imported-files";

    private MinioService minioService;
    private MultipartFile file;
    ImportHistory importHistory;
    private boolean prepared;

    private String fileUrl;

    public MinioTransactionParticipant(MinioService minioService, MultipartFile file, ImportHistory importHistory) {
        this.minioService = minioService;
        this.file = file;
        this.importHistory = importHistory;
        this.prepared = false;
    }

    @Override
    public boolean prepare() {
        try {
            minioService.testUploadFile(file);
            System.out.println("Minio: подготовлен к коммиту");
            prepared = true;
            return true;
        } catch (Exception e) {
            System.out.println("Minio: не готов к коммиту");
            prepared = false;
            return false;
        }
    }

    @Override
    public void commit() {
        if (prepared) {
            try {
                fileUrl = minioService.uploadFile(file, BUCKET_NAME);
                importHistory.setFileUrl(fileUrl);
                System.out.println("Minio: закоммитил изменения");
            } catch (Exception e) {
                System.out.println("Minio: ошибка при коммите");
                throw e;
            }
        } else {
            System.out.println("Minio: не готов к коммиту");
        }
    }

    @Override
    public void rollback() {
        if (prepared) {
            minioService.deleteFile(fileUrl, BUCKET_NAME);
            importHistory.setFileUrl(null);
            System.out.println("Minio: изменения были откачены");
            // возможно стоит удалить тестовый бакет
        } else {
            System.out.println("Minio: в rollback нечего откатывать");
        }
    }
}

package com.djeno.backend_lab1.service.transactions;

import com.djeno.backend_lab1.DTO.DataContainer;
import com.djeno.backend_lab1.models.ImportHistory;
import com.djeno.backend_lab1.service.ImportService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

//@Scope("prototype") // или заменить на request
//@Component
public class PostgresTransactionParticipant implements TransactionParticipant {

    private ImportService importService;
    private MultipartFile file;
    private ImportHistory importHistory;
    private boolean prepared;

    private DataContainer dataContainer;

    public PostgresTransactionParticipant(ImportService importService, MultipartFile file, ImportHistory importHistory) {
        this.importService = importService;
        this.file = file;
        this.importHistory = importHistory;
        this.prepared = false;
    }

    @Override
    public boolean prepare() {
        try {
            dataContainer = importService.parseYamlFileAndCheckConstraints(file);
            System.out.println("Postgres: подготовлен к коммиту");
            prepared = true;
            return true;
        } catch (Exception e) {
            System.out.println("Postgres: не готов к коммиту");
            prepared = false;
            return false;
        }
    }

    @Override
    public void commit() throws InterruptedException {
        if (prepared) {
            try {
                System.out.println("Postgres: 4 сек сна на ручное прерывание");
                Thread.sleep(4* 1000);

            } catch (Exception e) {
                System.out.println("Поток был прерван: " + e.getMessage());
                throw e;
            }

            try {
                int addedObjects = importService.saveData(dataContainer);
                importHistory.setAddedObjects(addedObjects);
                System.out.println("Postgres: закоммитил изменения");
            } catch (Exception e) {
                System.out.println("Postgres: ошибка при коммите");
                throw e;
            }
        } else {
            System.out.println("Postgres: не готов к коммиту");
        }
    }

    @Override
    public void rollback() {
        if (prepared) {
            // Полагаюсь на @Transactional
            importHistory.setAddedObjects(0);
            System.out.println("Postgres: изменения были откачены");
        } else {
            System.out.println("Postgres: в rollback нечего откатывать");
        }
    }
}

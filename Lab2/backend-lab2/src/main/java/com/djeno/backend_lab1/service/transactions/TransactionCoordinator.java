package com.djeno.backend_lab1.service.transactions;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public class TransactionCoordinator {
    private final List<TransactionParticipant> listOfParticipants;

    public TransactionCoordinator() {
        listOfParticipants = new ArrayList<>();
    }

    public void addParticipant(TransactionParticipant participant) {
        listOfParticipants.add(participant);
    }

    public void rollback() {
        try {
            for (TransactionParticipant participant : listOfParticipants) {
                participant.rollback();
            }
        } catch (Exception e) {
            System.out.println("Ошибка при откате транзакции: " + e.getMessage());
        }
    }

    public void execute() {
        boolean allPrepared = true;

        // prepare
        for (TransactionParticipant participant : listOfParticipants) {
            if(!participant.prepare()) {
                allPrepared = false;
                break;
            }
        }

        // commit
        if(allPrepared) {
            System.out.println("Участники готовы к коммиту. Коммит...");
            try {
                for (TransactionParticipant participant : listOfParticipants) {
                    participant.commit();
                }

            // rollback во время ошибки при коммите
            } catch (Exception e) {
                System.out.println("Ошибка при общем коммите: " + e.getMessage() + " Откат...");
                rollback();
                throw new RuntimeException("Transaction failed");
            }

        // rollback, если участники не готовы
        } else {
            System.out.println("Участники не готовы, откат...");
            rollback();
            throw new RuntimeException("Transaction failed");
        }
    }

}

package com.djeno.backend_lab1.service.transactions;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TransactionCoordinator {
    private final List<TransactionParticipant> listOfParticipants;

    public TransactionCoordinator() {
        listOfParticipants = new ArrayList<>();
    }

    public void addParticipant(TransactionParticipant participant) {
        listOfParticipants.add(participant);
    }

    public void execute() {
        boolean allPrepared = true;


        for (TransactionParticipant participant : listOfParticipants) {
            if(!participant.prepare()) {
                allPrepared = false;
                break;
            }
        }
        if(allPrepared) {
            System.out.println("Participant prepared, commit");
            for(TransactionParticipant participant : listOfParticipants) {
                participant.commit();
            }
        } else {
            System.out.println("Participant not prepared, rollback");
            for (TransactionParticipant participant : listOfParticipants) {
                participant.rollback();
            }
        }

        listOfParticipants.clear();
    }

}

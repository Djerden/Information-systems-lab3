package com.djeno.backend_lab1.service.transactions;

import org.springframework.stereotype.Component;

@Component
public class MinioTransactionParticipant implements TransactionParticipant {
    private boolean prepared = false;

    @Override
    public boolean prepare() {
        return false;
    }

    @Override
    public void commit() {

    }

    @Override
    public void rollback() {

    }
}

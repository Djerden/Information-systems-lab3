package com.djeno.backend_lab1.service.transactions;

public class PostgresTransactionParticipant implements TransactionParticipant{
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

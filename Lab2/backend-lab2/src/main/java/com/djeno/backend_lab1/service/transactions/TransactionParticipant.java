package com.djeno.backend_lab1.service.transactions;

public interface TransactionParticipant {
    boolean prepare();
    void commit();
    void rollback();
}

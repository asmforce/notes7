package com.asmx.data;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Consumer;

/**
 * User: asmforce
 * Timestamp: 22.11.15 17:42.
**/
public final class Transaction {
    private static PlatformTransactionManager transactionManager;

    public static <T> T run(TransactionCallback<T> callback) throws TransactionException {
        return new TransactionTemplate(transactionManager)
                .execute(callback);
    }

    public static void run(Consumer<TransactionStatus> callback) throws TransactionException {
        new TransactionTemplate(transactionManager).execute(transactionStatus -> {
            callback.accept(transactionStatus);
            return null;
        });
    }

    public static void setTransactionManager(PlatformTransactionManager transactionManager) {
        Transaction.transactionManager = transactionManager;
    }
}

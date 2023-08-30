package com.example.secerrordemo.infra.tx;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Component
class TxManagerImpl implements TxManager {

    private static final Logger log = LoggerFactory.getLogger(TxManagerImpl.class);
    private final TransactionTemplate newTransaction;

    TxManagerImpl(PlatformTransactionManager platformTransactionManager) {
        newTransaction = new TransactionTemplate(platformTransactionManager);
        newTransaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Override
    public void runInNewTransaction(@Nonnull TxRunnable runnable) {
        newTransaction.executeWithoutResult(tx -> {
            try {
                runnable.run();
            } catch (Throwable ex) {
                tx.setRollbackOnly();
                log.debug("Error in runInNewTransaction, rolling back transaction", ex);
                throw ex;
            }
        });
    }

    @Override
    public <R> R callInNewTransaction(@Nonnull TxCallable<R> callable) {
        return newTransaction.execute(tx -> {
            try {
                return callable.call();
            } catch (Throwable ex) {
                tx.setRollbackOnly();
                log.debug("Error in callInNewTransaction, rolling back transaction", ex);
                throw ex;
            }
        });
    }
}

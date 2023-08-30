package com.example.secerrordemo.infra.tx;

import jakarta.annotation.Nonnull;

public interface TxManager {

    void runInNewTransaction(@Nonnull TxRunnable runnable);

    <R> R callInNewTransaction(@Nonnull TxCallable<R> callable);
}

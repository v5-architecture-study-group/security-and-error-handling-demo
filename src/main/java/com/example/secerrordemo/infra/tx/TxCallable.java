package com.example.secerrordemo.infra.tx;

@FunctionalInterface
public interface TxCallable<R> {
    R call();
}

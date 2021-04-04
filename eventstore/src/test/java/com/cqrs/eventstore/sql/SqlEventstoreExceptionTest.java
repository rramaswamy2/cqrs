package com.cqrs.eventstore.sql;

public abstract class SqlEventstoreExceptionTest<T extends Exception> extends SqlEventStoreTest {
    protected T exception;
}

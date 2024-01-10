package com.levik.orm;

import com.levik.orm.repository.JdbcRepository;

import java.io.Closeable;

public interface BringSession extends Closeable {

    <T> T findById(Class<T> clazz, Object id);

    default void flush() {

    }

    JdbcRepository getJdbcRepository();
}

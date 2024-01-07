package com.levik.orm;

import com.levik.orm.repository.JdbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class Session implements Closeable {

    private final JdbcRepository jdbcRepository;

    public <T> T findById(Class<T> clazz, Object id) {
        return jdbcRepository.findById(clazz, id);
    }

    @Override
    public void close() {
        log.info("Close session...");
    }
}

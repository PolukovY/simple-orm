package com.levik.orm;

import com.levik.orm.repository.JdbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class Session implements BringSession {

    protected final JdbcRepository jdbcRepository;

    @Override
    public <T> T findById(Class<T> clazz, Object id) {
        return jdbcRepository.findById(clazz, id);
    }

    @Override
    public JdbcRepository getJdbcRepository() {
        return jdbcRepository;
    }

    @Override
    public void close() {
        log.info("Close session...");
    }
}

package com.levik.orm;

import com.levik.orm.repository.JdbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class Session implements BringSession {

    private final JdbcRepository jdbcRepository;

    @Override
    public <T> T findById(Class<T> clazz, Object id) {
        return jdbcRepository.findById(clazz, id);
    }

    @Override
    public void close() {
        log.info("Close session...");
    }
}

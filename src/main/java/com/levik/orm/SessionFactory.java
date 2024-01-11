package com.levik.orm;

import com.levik.orm.repository.JdbcRepository;
import com.levik.orm.repository.SqlBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.Closeable;

@Slf4j
public class SessionFactory  implements Closeable {

    private final JdbcRepository jdbcRepository;

    public SessionFactory(DataSource dataSource) {
        jdbcRepository = new JdbcRepository(dataSource, new SqlBuilder());
    }

    public BringSession openSession() {
        return new FirstLevelCacheSession(new Session(jdbcRepository));
    }

    @Override
    public void close() {
        log.info("Close session factory...");
    }
}

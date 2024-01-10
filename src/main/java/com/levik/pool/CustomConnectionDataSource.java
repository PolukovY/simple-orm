package com.levik.pool;

import lombok.SneakyThrows;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Queue;

public class CustomConnectionDataSource extends PGSimpleDataSource {

    private static final int DEFAULT_CONNECTION_POOL_SIZE = 10;
    private final Queue<ConnectionWrapper> connectionPool = new ArrayDeque<>();

    @SneakyThrows
    public CustomConnectionDataSource(String url, String username, String password) {
        setURL(url);
        setUser(username);
        setPassword(password);

        createConnectionPool(url, username, password, DEFAULT_CONNECTION_POOL_SIZE);
    }

    @SneakyThrows
    public CustomConnectionDataSource(String url, String username, String password, int poolSize) {
        setURL(url);
        setUser(username);
        setPassword(password);

        createConnectionPool(url, username, password, poolSize);
    }

    @SneakyThrows
    private void createConnectionPool(String url, String username,  String password, int poolSize) {
        for (int i = 0; i < poolSize; i++) {
            var connection = DriverManager.getConnection(url, username, password);
            var connectionWrapper = new ConnectionWrapper(connection, connectionPool);
            connectionPool.add(connectionWrapper);
        }
    }
}

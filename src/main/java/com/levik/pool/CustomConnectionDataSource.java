package com.levik.pool;

import lombok.SneakyThrows;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.logging.Logger;

public class CustomConnectionDataSource implements DataSource {

    private static final int DEFAULT_CONNECTION_POOL_SIZE = 10;
    private final Queue<ConnectionWrapper> connectionPool = new ArrayDeque<>();

    private final String url;
    private final String username;
    private final String password;

    @SneakyThrows
    public CustomConnectionDataSource(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;

        createConnectionPool(DEFAULT_CONNECTION_POOL_SIZE);
    }

    @SneakyThrows
    public CustomConnectionDataSource(String url, String username, String password, int poolSize) {
        this.url = url;
        this.username = username;
        this.password = password;

        createConnectionPool(poolSize);
    }

    @SneakyThrows
    private void createConnectionPool(int poolSize) {
        for (int i = 0; i < poolSize; i++) {
            var connection = DriverManager.getConnection(url, username, password);
            var connectionWrapper = new ConnectionWrapper(connection, connectionPool);
            connectionPool.add(connectionWrapper);
        }
    }

    @Override
    public Connection getConnection() {
        return connectionPool.poll();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }
}

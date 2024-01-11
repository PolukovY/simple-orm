package com.levik.orm.repository;

import com.levik.orm.exception.EntityNotFound;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.Objects;

import static com.levik.orm.annotation.EntityUtils.*;

@RequiredArgsConstructor
@Slf4j
public class JdbcRepository {
    private static final String NOT_FOUND_RESULT_BY_ID = "Entity '%s' with the %s '%s' in the '%s' table returned no results.";

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;

    @SneakyThrows
    public <T> T findById(Class<T> clazz, Object id) {
        Objects.requireNonNull(clazz, "Clazz must be not null");
        Objects.requireNonNull(id, "Id must be not null");

        var tableName = table(clazz);
        var fieldIdName = fieldIdName(clazz);

        var query = sqlBuilder.selectById(tableName, fieldIdName);

        try (var connection = dataSource.getConnection()) {
            log.info("Query {}", query);
            try (var statement = connection.prepareStatement(query)) {
                statement.setObject(1, id);
                var resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return clazz.cast(toEntity(clazz, resultSet));
                }
            }
        }


        throw new EntityNotFound(NOT_FOUND_RESULT_BY_ID.formatted(clazz.getSimpleName(), fieldIdName, id, tableName));
    }


    @SneakyThrows
    public <T> T update(Class<T> clazz, Object entity) {
        Objects.requireNonNull(clazz, "Clazz must be not null");
        Objects.requireNonNull(entity, "Entity must be not null");

        var tableName = table(clazz);
        var fieldIdName = fieldIdName(clazz);
        var fieldIdValue = fieldIdValue(clazz, entity);

        String query  = sqlBuilder.update(entity, tableName, fieldIdName);

        try (var connection = dataSource.getConnection()) {
            log.info("Query: {}", query);
            try (var statement = connection.prepareStatement(query)) {
                populatePreparedStatement(entity, statement, fieldIdName, fieldIdValue);
                var resultSet = statement.executeUpdate();
                log.info("Update effected row {} for entity clazz {} with id {}", resultSet, clazz.getSimpleName(), fieldIdValue);
            }
        }

        return clazz.cast(entity);
    }

    @SneakyThrows
    private void populatePreparedStatement(Object entity, PreparedStatement statement,
                                           String fieldIdName, Object fieldIdValue) {
        int parameterIndex = 1;
        for (var field : entity.getClass().getDeclaredFields()) {
            if (!fieldIdName.equals(fieldName(field))) {
                var fieldValue = getValueFromObject(entity, field);
                statement.setObject(parameterIndex++, fieldValue);
            }
        }
        statement.setObject(parameterIndex, fieldIdValue);
    }
}

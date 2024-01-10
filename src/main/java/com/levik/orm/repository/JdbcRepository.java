package com.levik.orm.repository;

import com.levik.orm.exception.EntityNotFound;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.levik.orm.annotation.EntityUtils.*;

@RequiredArgsConstructor
@Slf4j
public class JdbcRepository {

    private static final String NOT_FOUND_RESULT_BY_ID = "Entity '%s' with the %s '%s' in the '%s' table returned no results.";

    private static final String SELECT_TABLE_BY_ID = """
            select * from %s where %s = ?
            """;

    private static final String UPDATE_TABLE_BY_ID = "UPDATE %s SET %s WHERE %s = ?";


    private final DataSource dataSource;

    @SneakyThrows
    public <T> T findById(Class<T> clazz, Object id) {
        Objects.requireNonNull(clazz, "Clazz must be not null");
        Objects.requireNonNull(id, "Id must be not null");

        var tableName = table(clazz);
        var fieldIdName = fieldIdName(clazz);

        var query = SELECT_TABLE_BY_ID.formatted(tableName, fieldIdName);

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
    public <T> T insert(Class<T> clazz, Object entity) {
        Objects.requireNonNull(clazz, "Clazz must be not null");
        Objects.requireNonNull(entity, "Entity must be not null");

        var tableName = table(clazz);
        var fieldIdName = fieldIdName(clazz);
        var fieldIdValue = fieldIdValue(clazz, entity);

        String query  = buildInsertQuery(entity, tableName, fieldIdName);

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
    private static void populatePreparedStatement(Object entity, PreparedStatement statement,
                                                 String fieldIdName, Object fieldIdValue) {
        int parameterIndex = 1;
        for (var field : entity.getClass().getDeclaredFields()) {
            if (!fieldIdName.equals(fieldName(field))) {
                field.setAccessible(true);
                var fieldValue = field.get(entity);
                statement.setObject(parameterIndex++, fieldValue);
            }
        }
        statement.setObject(parameterIndex, fieldIdValue);
    }

    private String buildInsertQuery(Object entity, String tableName, String fieldIdName) {
        var declaredFields = entity.getClass().getDeclaredFields();
        String setFields = Arrays.stream(declaredFields)
                .filter(field -> !fieldName(field).equals(fieldIdName))
                .map(field -> fieldName(field) + " = ?")
                .collect(Collectors.joining(", "));

       return UPDATE_TABLE_BY_ID.formatted(tableName, setFields, fieldIdName);
    }
}

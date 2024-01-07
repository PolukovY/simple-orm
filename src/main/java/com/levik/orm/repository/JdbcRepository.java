package com.levik.orm.repository;

import com.levik.orm.exception.EntityNotFound;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;

import static com.levik.orm.annotation.EntityUtils.table;
import static com.levik.orm.annotation.EntityUtils.toEntity;
import static com.levik.orm.annotation.EntityUtils.fieldIdName;

@RequiredArgsConstructor
@Slf4j
public class JdbcRepository {

    private static final String NOT_FOUND_RESULT_BY_ID = "Entity '%s' with the %s '%s' in the '%s' table returned no results.";

    private static final String SELECT_TABLE_BY_ID = """
            select * from %s where %s = ?
            """;

    private final DataSource dataSource;

    @SneakyThrows
    public <T> T findById(Class<T> clazz, Object id) {
        Objects.requireNonNull(clazz, "Clazz must be not null");
        Objects.requireNonNull(id, "Id must be not null");

        var tableName = table(clazz);
        var fieldIdName = fieldIdName(clazz);

        var query = SELECT_TABLE_BY_ID.formatted(tableName, fieldIdName);

        try (var connection = dataSource.getConnection()) {
            try(var statement = connection.prepareStatement(query)) {
                statement.setObject(1, id);
                var resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return clazz.cast(toEntity(clazz, resultSet));
                }
            }
        }


        throw new EntityNotFound(NOT_FOUND_RESULT_BY_ID.formatted(clazz.getSimpleName(), fieldIdName, id, tableName));
    }
}

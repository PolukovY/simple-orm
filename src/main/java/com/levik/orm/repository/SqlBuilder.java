package com.levik.orm.repository;

import com.levik.orm.annotation.EntityUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SqlBuilder {

    private static final String SELECT_TABLE_BY_ID = """
            select * from %s where %s = ?
            """;

    private static final String UPDATE_TABLE_BY_ID = "UPDATE %s SET %s WHERE %s = ?";
    private static final String PARAMETER = " = ?";
    private static final String COMA = ", ";

    public String selectById(String tableName, String fieldIdName) {
        return SELECT_TABLE_BY_ID.formatted(tableName, fieldIdName);
    }

    public String update(Object entity, String tableName, String fieldIdName) {
        var declaredFields = entity.getClass().getDeclaredFields();
        String setFields = Arrays.stream(declaredFields)
                .map(EntityUtils::fieldName)
                .filter(fieldName -> !fieldName.equals(fieldIdName))
                .map(fieldName -> fieldName + PARAMETER)
                .collect(Collectors.joining(COMA));

        return UPDATE_TABLE_BY_ID.formatted(tableName, setFields, fieldIdName);
    }
}

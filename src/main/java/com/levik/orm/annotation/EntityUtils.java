package com.levik.orm.annotation;

import com.levik.orm.exception.EntityIdentityNotFound;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Arrays;

import static java.util.Optional.ofNullable;

@UtilityClass
public class EntityUtils {

    public static String table(Class<?> clazz) {
        return ofNullable(clazz.getAnnotation(Table.class))
                .map(Table::value)
                .orElse(clazz.getSimpleName());
    }

    public static String fieldName(Field field) {
        return ofNullable(field.getAnnotation(Column.class))
                .map(Column::value)
                .orElse(field.getName());
    }

    public static String fieldIdName(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .map(field -> ofNullable(field.getAnnotation(Column.class))
                        .map(Column::value)
                        .orElse(field.getName()))
                .findFirst()
                .orElseThrow(() -> new EntityIdentityNotFound("Id not found in entity " + clazz.getSimpleName()));
    }

    @SneakyThrows
    public static Object toEntity(Class<?> clazz, ResultSet resultSet) {
        var declaredConstructor = clazz.getDeclaredConstructor();
        var obj = declaredConstructor.newInstance();

        for (var field : clazz.getDeclaredFields()) {
            var fieldName = fieldName(field);
            var value = resultSet.getObject(fieldName);

            field.setAccessible(true);
            field.set(obj, value);
        }

        return obj;
    }
}

package com.levik.orm.annotation;

import com.levik.orm.exception.EntityIdentityNotFound;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Objects;

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

    public static Class<?> fieldIdType(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .map(Field::getType)
                .findFirst()
                .orElseThrow(() -> new EntityIdentityNotFound("Id not found in entity " + clazz.getSimpleName()));
    }

    public static <T> T castIdToEntityId(Class<T> clazz, Object id) {
        Objects.requireNonNull(clazz, "Clazz must be not null");
        Objects.requireNonNull(id, "Id must be not null");

        var fieldIdType = fieldIdType(clazz);

        if (!fieldIdType.isInstance(id)) {
            id = convertToType(id, fieldIdType);
        }

        return (T) id;
    }

    private static Object convertToType(Object value, Class<?> targetType) {
        if (value instanceof Number) {
            Number number = (Number) value;
            if (targetType.equals(Byte.class)) {
                return number.byteValue();
            } else if (targetType.equals(Short.class)) {
                return number.shortValue();
            } else if (targetType.equals(Integer.class)) {
                return number.intValue();
            } else if (targetType.equals(Long.class)) {
                return number.longValue();
            } else if (targetType.equals(Float.class)) {
                return number.floatValue();
            } else if (targetType.equals(Double.class)) {
                return number.doubleValue();
            }
        } else if (value instanceof Boolean && targetType.equals(Boolean.class)) {
            return (boolean) value;
        } else if (value instanceof Character && targetType.equals(Character.class)) {
            return (char) value;
        }
        // Add more conditions for other types if needed
        return value;
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

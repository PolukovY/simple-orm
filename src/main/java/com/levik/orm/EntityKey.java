package com.levik.orm;

import java.lang.reflect.Type;

public record EntityKey<T>(Class<T> clazz, Object id, Type keyType) {
}

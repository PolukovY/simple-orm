package com.levik.orm;

import java.io.Closeable;

public interface BringSession extends Closeable {

    <T> T findById(Class<T> clazz, Object id);
}

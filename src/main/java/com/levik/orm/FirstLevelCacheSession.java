package com.levik.orm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.levik.orm.annotation.EntityUtils.castIdToEntityId;
import static com.levik.orm.annotation.EntityUtils.fieldIdType;

@RequiredArgsConstructor
@Slf4j
public class FirstLevelCacheSession implements BringSession {

    private final BringSession bringSession;
    private final Map<EntityKey<?>, Object> firstLevelCache = new HashMap<>();

    @Override
    public <T> T findById(Class<T> clazz, Object id) {
        var fieldIdType = fieldIdType(clazz);
        id  = castIdToEntityId(clazz, id);
        var entityKey = new EntityKey<>(clazz, id, fieldIdType);
        var cachedEntity = firstLevelCache.get(entityKey);

        if (Objects.isNull(cachedEntity)) {
            T entityFromDb = bringSession.findById(clazz, id);
            firstLevelCache.put(entityKey, entityFromDb);
            log.info("Entity {} not found in firstLevel cache by id {}", clazz.getSimpleName(), id);
            return entityFromDb;
        }

        log.info("Entity {} found in firstLevel cache by id {}", clazz.getSimpleName(), id);

        return clazz.cast(cachedEntity);
    }

    @Override
    public void close() {
        firstLevelCache.clear();
    }
}

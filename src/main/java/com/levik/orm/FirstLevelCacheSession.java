package com.levik.orm;

import com.levik.orm.repository.JdbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.levik.orm.annotation.EntityUtils.*;

@RequiredArgsConstructor
@Slf4j
public class FirstLevelCacheSession implements BringSession {

    private final BringSession bringSession;
    private final Map<EntityKey<?>, Object> firstLevelCache = new HashMap<>();
    private final Map<EntityKey<?>, Object[]> snapshots = new HashMap<>();

    @Override
    public <T> T findById(Class<T> clazz, Object id) {
        var fieldIdType = fieldIdType(clazz);
        id  = castIdToEntityId(clazz, id);
        var entityKey = new EntityKey<>(clazz, id, fieldIdType);
        var cachedEntity = firstLevelCache.get(entityKey);

        if (Objects.isNull(cachedEntity)) {
            T entityFromDb = bringSession.findById(clazz, id);
            firstLevelCache.put(entityKey, entityFromDb);

            Object[] entityCurrentSnapshot = makeCurrentEntitySnapshot(entityFromDb);
            snapshots.put(entityKey, entityCurrentSnapshot);
            log.info("Create snapshot for entity {} id {}", clazz.getSimpleName(), id);
            log.info("Entity {} not found in firstLevel cache by id {}", clazz.getSimpleName(), id);
            return entityFromDb;
        }

        log.info("Entity {} found in firstLevel cache by id {}", clazz.getSimpleName(), id);

        return clazz.cast(cachedEntity);
    }

    private <T> void insert(Class<T> clazz, Object entity) {
        var fieldIdType = fieldIdType(clazz);
        var fieldIdValue = fieldIdValue(clazz, entity);

        var entityKey = new EntityKey<>(clazz, fieldIdValue, fieldIdType);
        T insertEntityFromDb = getJdbcRepository().insert(clazz, entity);
        firstLevelCache.put(entityKey, insertEntityFromDb);
        log.info("Update Entity {} in firstLevel cache by id {}", clazz.getSimpleName(), fieldIdValue);
        Object[] entityCurrentSnapshot = makeCurrentEntitySnapshot(insertEntityFromDb);
        snapshots.put(entityKey, entityCurrentSnapshot);
        log.info("Update snapshot for entity {} id {}", clazz.getSimpleName(), fieldIdValue);
    }

    @Override
    public void flush() {
        checkDirtyCheckingEntities();
    }

    @Override
    public JdbcRepository getJdbcRepository() {
        return bringSession.getJdbcRepository();
    }

    private void checkDirtyCheckingEntities() {
        for (EntityKey<?> entityKey : firstLevelCache.keySet()) {
            Object entityInFirstLevelCache = firstLevelCache.get(entityKey);
            Object[] entityInFirstLevelCacheCurrentSnapshot = makeCurrentEntitySnapshot(entityInFirstLevelCache);
            Object[] entityOldSnapshot = snapshots.get(entityKey);

            if (!isCurrentSnapshotAndOldSnapshotTheSame(entityInFirstLevelCacheCurrentSnapshot, entityOldSnapshot)) {
                log.info("Dirty entity found need to generate update for entityKey {} and entity {}", entityKey, entityInFirstLevelCache);
                insert(entityInFirstLevelCache.getClass(),entityInFirstLevelCache);
            } else {
                log.info("Dirty entity not found for entityKey {} no changes", entityKey);
            }
        }
    }

    @Override
    public void close() {
        log.info("Session is closing. Need to check do I have dirtyCheckin entities.");
        checkDirtyCheckingEntities();
        log.info("FirstLevelCache is clearing...");
        firstLevelCache.clear();

    }
}

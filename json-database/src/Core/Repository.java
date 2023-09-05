package Core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@SuppressWarnings("all")
public abstract class Repository<TEntity extends EntityBase> implements IRepository<TEntity, UUID> {

    private Database<TEntity> _database;
    private static UUID lastOperatedId;

    public Repository() {
        _database = getDatabase();
    }

    protected Database<TEntity> getDatabase() {
        Type t = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) t;
        Class<TEntity> type = (Class) pt.getActualTypeArguments()[0];

        _database = (Database<TEntity>) Database.getInstance();
        _database.configureType(type);

        return _database;
    }

    public static UUID getLastOperatedId() {
        return lastOperatedId;
    }

    @Override
    public TEntity getById(UUID id) {
        List<TEntity> entity = getDatabase().get().filter(e -> e.getId().equals(id)).toList();
        
        if (entity.size() == 1) {
            return entity.get(0);
        }

        return null;
    }
    
    @Override
    public List<TEntity> getAll() {
        List<TEntity> entities = getDatabase().get().toList();

        if (entities.size() >= 1) {
            return new ArrayList<>(entities);
        }

        return null;
    }

    @Override
    public Object deleteById(UUID id) {

        try {

            var entities = getAll();
            var removed = entities.removeIf(x -> x.getId().equals(id));
            this.getDatabase().set(entities);

            lastOperatedId = removed ? id : lastOperatedId;

            return removed;

        }  catch (Exception e) {
            System.out.println(e.getMessage() + "Err");
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public UUID save(TEntity entity) {
        try {
            List<TEntity> entities = getAll();

            if (entities == null) {
                
                entities = new ArrayList<>();
                entities.add(entity);

            } else {

                if (entities.stream().anyMatch(x -> x.getId().equals(entity.getId()))) {

                    final var referenceEntities = entities;
                    entities.forEach(entity_ -> {

                        if (entity_.getId().equals(entity.getId())) {
                            lastOperatedId = entity.getId();
                            int index = referenceEntities.indexOf(entity_);
                            referenceEntities.set(index, entity);
                        }

                    });

                } else {
                    entities.add(entity);
                }

            }

            getDatabase().set(assertIds(entities));

            return lastOperatedId;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private List<TEntity> assertIds(List<TEntity> data) {
        data.forEach(entity -> {
            if (entity.getId() == null) {
                var id = UUID.nameUUIDFromBytes(LocalDateTime.now().toString().getBytes());
                entity.setId(id);
                lastOperatedId = id;
            }
        });
        return data;
    }

}

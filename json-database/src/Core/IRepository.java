import java.util.List;

/**
 * An interface representing a generic repository for storing and retrieving entities.
 *
 * @param <TEntity> The type of entities stored in the repository.
 * @param <TKey>    The type of the entity's primary key.
 */
public interface IRepository<TEntity extends EntityBase, TKey> {

    /**
     * Saves or update an entity to the repository. 
     * 
     * Create new record on database if id is null.
     * Otherwise, updates entity.
     *
     * @param entity The entity to save.
     * @return The primary key of the saved entity.
     * 
     */
    TKey save(TEntity entity);

    /**
     * Retrieves an entity by its primary key.
     *
     * @param id The primary key of the entity to retrieve.
     * @return The retrieved entity, or null if not found.
     */
    TEntity getById(TKey id);

    /**
     * Retrieves all entities from the repository.
     *
     * @return A list of all entities in the repository.
     */
    List<TEntity> getAll();

    /**
     * Deletes an entity by its primary key.
     *
     * @param id The primary key of the entity to delete.
     * @return true if the entity was deleted successfully, false otherwise.
     */
    Object deleteById(TKey id);

}

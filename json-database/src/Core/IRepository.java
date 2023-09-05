package Core;
import java.util.List;

public interface IRepository<TEntity extends EntityBase, TKey> {

    TKey save(TEntity entity);

    TEntity getById(TKey id);

    List<TEntity> getAll();

    Object deleteById(TKey id);

}

package data.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {
    Optional<T> find(long id);
    void save(T entity);
    void update(long id, T entity);
    void delete(long id);
    List<T> getAll();
}

package org.example.jwt.jdbc.api;

import java.util.Optional;

public interface JdbcDao<T> {
    T insert(T entity);

    T update(T entity);

    void delete(T entity);

    Optional<T> findOne(String uuid);
}

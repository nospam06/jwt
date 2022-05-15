package org.example.jwt.jdbc.api;

import org.example.jwt.dto.UserDto;

import java.util.List;
import java.util.Map;

public interface JdbcDao<T> {
    UserDto insert(T entity);

    UserDto update(T entity);

    void delete(T entity);

    T findOne(String uuid);

    List<T> findAll(Class<T> cls, Map<String, String> searchCriteria);
}

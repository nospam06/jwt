package org.example.jwt.nosql.api;

import org.example.jwt.model.BaseEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CrudRepository {
	<T extends BaseEntity, U extends Serializable> Optional<T> findById(Class<T> clazz, U id);

	<T extends BaseEntity, U extends Serializable> List<T> findAllByIds(Class<T> clazz, Collection<U> ids);

	<T extends BaseEntity> boolean insert(T entity);

	<T extends BaseEntity> void save(T entity);

	<T extends BaseEntity> void save(Collection<T> entities);

	<T extends BaseEntity, U extends Serializable> void remove(Class<T> clazz, U id);

	<T extends BaseEntity, U extends Serializable> void remove(Class<T> clazz, Collection<U> ids);
}

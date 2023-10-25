package org.example.jwt.nosql.api;

import org.example.jwt.model.BaseEntity;
import org.example.jwt.nosql.query.QueryParameter;

import java.util.List;

public interface NoSqlRepository extends CrudRepository {
	<T extends BaseEntity> List<T> findAll(Class<T> clazz);

	<T extends BaseEntity> List<T> findAll(Class<T> clazz, QueryParameter parameter);
}
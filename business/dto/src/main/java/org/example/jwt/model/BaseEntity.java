package org.example.jwt.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class BaseEntity implements Identifiable<String> {
	@JsonProperty(value = "_format")
	public String getType() {
		Class<?> clz = getConcreteClass();
		NoSqlEntity annotation = clz.getAnnotation(NoSqlEntity.class);
		return annotation.type();
	}

	protected abstract Class<?> getConcreteClass();
}

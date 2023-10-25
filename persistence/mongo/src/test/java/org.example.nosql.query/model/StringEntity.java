package org.example.nosql.query.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.jwt.model.BaseEntity;
import org.example.jwt.model.NoSqlEntity;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@NoSqlEntity(type = "testString", index = "test")
public class StringEntity extends BaseEntity implements Serializable {
	private String id;
	private String name;

	@Override
	@JsonIgnore
	protected Class<?> getConcreteClass() {
		return getClass();
	}
}

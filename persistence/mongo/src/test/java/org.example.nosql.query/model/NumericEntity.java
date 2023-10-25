package org.example.nosql.query.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.jwt.model.BaseEntity;
import org.example.jwt.model.NoSqlEntity;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@NoSqlEntity(type = "testNumber", index = "test", idNumeric = true)
public class NumericEntity extends BaseEntity implements Serializable {
	private String id;
	private Long nid;
	private String name;
	private double value;

	@Override
	@JsonIgnore
	protected Class<?> getConcreteClass() {
		return getClass();
	}

	public String getId() {
		return Long.toString(nid);
	}
}

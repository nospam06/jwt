package org.example.nosql.query.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.jwt.model.AutoExpire;
import org.example.jwt.model.BaseEntity;
import org.example.jwt.model.NoSqlEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoSqlEntity(type = "geo", index = "test")
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class GeoEntity extends BaseEntity implements AutoExpire, Serializable {
	private String id;
	private String name;
	@JsonProperty("_latitude")
	private Double taLatitude;
	@JsonProperty("_longitude")
	private Double taLongitude;
	private int intValue;
	private double doubleValue;
	private BigDecimal decimalValue;
	private String numberString;
	private NoneEntity nestedObject;
	private int expiry;
	@Builder.Default
	private Instant createDateTime = Instant.now();
	private Boolean boolValue;

	@Override
	@JsonIgnore
	protected Class<?> getConcreteClass() {
		return getClass();
	}
}

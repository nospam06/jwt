package org.example.nosql.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.jwt.json.config.JsonConfig;
import org.example.nosql.query.model.GeoEntity;
import org.example.nosql.query.model.NoneEntity;

import java.math.BigDecimal;
import java.util.List;

public class JsonBaseTest {
	protected final ObjectMapper objectMapper = new JsonConfig().objectMapper();

	protected GeoEntity createEntity(String id, String name, Double lat, Double lon, int i, double d, BigDecimal decimal, String numberString) {
		return GeoEntity.builder().id(id).name(name).taLatitude(lat).taLongitude(lon).intValue(i).doubleValue(d).decimalValue(decimal).numberString(numberString).nestedObject(new NoneEntity(id, name, List.of(new NoneEntity("hello", "world", null)))).build();
	}
}

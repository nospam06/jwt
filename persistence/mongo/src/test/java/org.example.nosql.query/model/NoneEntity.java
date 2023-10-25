package org.example.nosql.query.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoneEntity implements Serializable {
	private String id;
	private String name;
	private List<NoneEntity> tags;
}

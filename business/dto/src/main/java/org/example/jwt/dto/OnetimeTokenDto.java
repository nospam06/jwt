package org.example.jwt.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.jwt.model.BaseEntity;
import org.example.jwt.model.NoSqlEntity;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@NoSqlEntity(type = "onetime")
public class OnetimeTokenDto extends BaseEntity {
    private String token;
    private String email;
    private Instant createDate;
    private Instant expirationDate;
    private Instant usedDate;

    @Override
    protected Class<?> getConcreteClass() {
        return getClass();
    }

    @Override
    public String getId() {
        return token;
    }
}

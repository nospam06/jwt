package org.example.jwt.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.jwt.model.BaseEntity;
import org.example.jwt.model.NoSqlEntity;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@NoSqlEntity(type = "usertoken")
public class UserTokenDto extends BaseEntity {
    private String uuid;
    private String userUuid;
    private String token;
    private Instant createDate;
    private Instant expirationDate;

    @Override
    protected Class<?> getConcreteClass() {
        return getClass();
    }

    @Override
    public String getId() {
        return uuid;
    }
}

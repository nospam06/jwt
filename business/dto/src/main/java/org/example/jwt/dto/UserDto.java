package org.example.jwt.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.jwt.model.BaseEntity;
import org.example.jwt.model.NoSqlEntity;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@NoSqlEntity(type = "user")
public class UserDto extends BaseEntity {
    private String uuid;
    @JsonIgnore
    private String password;
    private String passwordSha;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Instant createDate;
    private Instant updateDate;

    @Override
    protected Class<?> getConcreteClass() {
        return getClass();
    }

    @Override
    public String getId() {
        return uuid;
    }
}

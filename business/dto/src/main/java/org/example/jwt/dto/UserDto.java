package org.example.jwt.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.Instant;

@Data
public class UserDto {
    private String uuid;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String passwordSha;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Instant createDate;
    private Instant updateDate;
}

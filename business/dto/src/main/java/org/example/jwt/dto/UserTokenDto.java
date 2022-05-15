package org.example.jwt.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class UserTokenDto {
    private String uuid;
    private String userUuid;
    private Instant createDate;
    private Instant expirationDate;
}

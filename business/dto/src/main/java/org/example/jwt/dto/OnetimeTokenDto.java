package org.example.jwt.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class OnetimeTokenDto {
    private String token;
    private String email;
    private Instant createDate;
    private Instant expirationDate;
    private Instant usedDate;
}

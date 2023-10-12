package org.example.jwt.jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.Instant;

@Data
@Entity(name = "user_token")
public class UserToken {
    @Id
    private String uuid;
    @Column(name = "user_uuid")
    private String userUuid;
    @Column(length = 4096)
    private String token;
    @Column(name = "create_date")
    private Instant createDate;
    @Column(name = "expiration_date")
    private Instant expirationDate;
}

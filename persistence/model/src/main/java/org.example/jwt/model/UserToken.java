package org.example.jwt.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Data
@Entity(name = "user_token")
public class UserToken {
    @Id
    private String uuid;
    @Column(name = "user_uuid")
    private String userUuid;
    private String token;
    @Column(name = "create_date")
    private Instant createDate;
    @Column(name = "expiration_date")
    private Instant expirationDate;
}

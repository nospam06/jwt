package org.example.jwt.jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.Instant;

@Data
@Entity(name = "onetime_token")
public class OnetimeToken {
    @Id
    private String token;
    private String email;
    @Column(name = "create_date")
    private Instant createDate;
    @Column(name = "expiration_date")
    private Instant expirationDate;
    @Column(name = "used_date")
    private Instant usedDate;
}

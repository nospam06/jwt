package org.example.jwt.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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

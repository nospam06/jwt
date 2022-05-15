package org.example.jwt.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Data
@Entity(name = "user")
public class User {
    @Id
    private String uuid;
    @Column(name  = "password")
    private String passwordSha;
    @Column(name  = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String email;
    private String phone;
    @Column(name = "create_date")
    private Instant createDate;
    @Column(name = "update_date")
    private Instant updateDate;
}

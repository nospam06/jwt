package org.example.jwt.dto;

import lombok.Data;

@Data
public class UserRequest {
    private String onetimeToken;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String password;
}

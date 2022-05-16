package org.example.jwt.logic.api;

import org.example.jwt.dto.OnetimeTokenDto;
import org.example.jwt.dto.UserDto;
import org.example.jwt.dto.UserLoginRequest;
import org.example.jwt.dto.UserRequest;
import org.example.jwt.dto.UserResponse;

public interface UserService {
    OnetimeTokenDto createOnetimeToken(String email);

    UserResponse createUser(UserRequest request);

    UserResponse login(UserLoginRequest request);

    UserDto findOne(String uuid);

    UserDto findByEmail(String email);
}

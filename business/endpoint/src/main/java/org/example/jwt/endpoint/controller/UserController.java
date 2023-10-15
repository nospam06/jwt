package org.example.jwt.endpoint.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jwt.dto.UserDto;
import org.example.jwt.dto.UserLoginRequest;
import org.example.jwt.dto.UserLoginResponse;
import org.example.jwt.logic.UserException;
import org.example.jwt.logic.api.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/api/jwt/user")
public class UserController {
    private final UserService userService;

    @GetMapping
    public UserDto findByEmail(@RequestParam String email, Authentication authentication) {
        if (!StringUtils.hasText(email)) {
            throw new UserException("email is required", null);
        }
        return userService.findByEmail(email);
    }

    @GetMapping(value = "/{uuid}")
    public UserDto findOne(@PathVariable String uuid) {
        return userService.findOne(uuid);
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserLoginResponse login(@RequestBody UserLoginRequest request) {
        return userService.login(request);
    }
}

package org.example.jwt.endpoint.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jwt.dto.OnetimeTokenDto;
import org.example.jwt.dto.UserLoginRequest;
import org.example.jwt.dto.UserRequest;
import org.example.jwt.dto.UserResponse;
import org.example.jwt.logic.api.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/api/jwt/token", produces = MediaType.APPLICATION_JSON_VALUE)
public class TokenController {
    private final UserService userService;

    @GetMapping
    public String signup(@RequestParam String email) {
        OnetimeTokenDto onetimeToken = userService.createOnetimeToken(email);
        log.info("one time token created {} for {}", onetimeToken.getToken(), email);
        // this should be done by email to confirm user identity
        return "/myapp/login.html?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8)
                + "&token=" + URLEncoder.encode(onetimeToken.getToken(), StandardCharsets.UTF_8);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public String newUser(@RequestBody UserRequest request) {
        userService.createUser(request);
        return "user created. please login using your email and password";
    }

    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse login(@RequestBody UserLoginRequest request) {
        return userService.login(request);
    }
}

package org.example.jwt.endpoint.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jwt.dto.*;
import org.example.jwt.logic.api.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/jwt/token", produces = MediaType.APPLICATION_JSON_VALUE)
public class TokenController {
    private final UserService userService;

    @GetMapping
    public OnetimeTokenResponse signup(@RequestParam String email) {
        OnetimeTokenDto onetimeToken = userService.createOnetimeToken(email);
        log.info("one time token created for {}", email);
        // this should be done by email to confirm user identity
        OnetimeTokenResponse userResponse = new OnetimeTokenResponse();
        userResponse.setOnetimeToken(onetimeToken.getToken());
        return userResponse;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserLoginResponse newUser(@RequestBody UserRequest request) {
        return userService.createUser(request);
    }
}

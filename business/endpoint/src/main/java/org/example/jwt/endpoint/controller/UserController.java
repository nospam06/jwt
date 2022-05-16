package org.example.jwt.endpoint.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jwt.dto.UserDto;
import org.example.jwt.logic.UserException;
import org.example.jwt.logic.api.UserService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public UserDto findByEmail(@RequestParam String email) {
        if (!StringUtils.hasText(email)) {
            throw new UserException("email is required", null);
        }
        return userService.findByEmail(email);
    }

    @GetMapping(value = "/{uuid}")
    public UserDto findOne(@PathVariable String uuid) {
        return userService.findOne(uuid);
    }
}

package org.example.jwt.security.api;

import org.example.jwt.dto.UserTokenDto;

public interface TokenService {
    String newToken(String userUuid);

    UserTokenDto verifyToken(String token);
}

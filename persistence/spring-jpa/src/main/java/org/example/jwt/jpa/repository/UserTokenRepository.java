package org.example.jwt.jpa.repository;

import org.example.jwt.jpa.model.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTokenRepository extends JpaRepository<UserToken, String> {
    List<UserToken> findByUserUuid(String userUuid);
}

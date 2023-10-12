package org.example.jwt.jpa.repository;

import org.example.jwt.jpa.model.OnetimeToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OnetimeTokenRepository extends JpaRepository<OnetimeToken, String> {
}

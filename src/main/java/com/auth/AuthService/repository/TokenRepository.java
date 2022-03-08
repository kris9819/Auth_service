package com.auth.AuthService.repository;

import com.auth.AuthService.domain.VerificationToken;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository {
    void createToken(String uuid, String token);
    VerificationToken findToken(String token);
    VerificationToken genNewToken(String token);
}

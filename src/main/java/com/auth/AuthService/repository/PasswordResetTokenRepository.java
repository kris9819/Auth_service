package com.auth.AuthService.repository;

import com.auth.AuthService.domain.PasswordResetToken;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository {
    void createToken(String uuid, String token);
    PasswordResetToken findToken(String token);
}

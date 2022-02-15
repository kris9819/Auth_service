package com.auth.AuthService.repository;

import com.auth.AuthService.domain.UserData;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    boolean emailExist(String email);
    void save(UserData userData);
    UserData findUserByUUID(String uuid);
    UserData findUserByEmail(String email);
    void changeUserPassword(String uuid, String password, PasswordEncoder passwordEncoder);
}

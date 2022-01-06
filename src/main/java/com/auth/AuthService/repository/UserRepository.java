package com.auth.AuthService.repository;

import com.auth.AuthService.domain.UserData;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    boolean emailExist(String email);
    void save(UserData userData);
}

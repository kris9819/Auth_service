package com.auth.AuthService.service;

import com.auth.AuthService.DTO.UserDTO;
import com.auth.AuthService.domain.UserData;

public interface IUserService {
    UserData registerUser(UserDTO userDTO);
}

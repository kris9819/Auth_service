package com.auth.AuthService.service;

import com.auth.AuthService.DTO.UserDTO;
import com.auth.AuthService.domain.BookList;
import com.auth.AuthService.domain.UserData;

public interface IUserService {
    UserData registerUser(UserDTO userDTO);
    UserData findByEmail(String email);
    String validatePasswordResetToken(String token);
    void updatePassword(String uuid, String password);
    void addUserBook(int bookId);
    BookList getUserBooks();
}

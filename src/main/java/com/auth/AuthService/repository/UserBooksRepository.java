package com.auth.AuthService.repository;

import com.auth.AuthService.domain.BookList;
import com.auth.AuthService.domain.UserBooks;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBooksRepository {
    void addUserBook(String uuid, int bookId);
    BookList getUserBooksArray(String uuid);
}

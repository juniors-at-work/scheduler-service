package com.example.scheduler.domain.repository;

import com.example.scheduler.domain.exception.EmailAlreadyExistException;
import com.example.scheduler.domain.exception.UsernameAlreadyExistException;
import com.example.scheduler.domain.model.User;

import java.util.Optional;

public interface UserRepository {

    User insert(User user) throws UsernameAlreadyExistException, EmailAlreadyExistException;

    Optional<User> findByUsername(String username);
}

package com.rapifuzz.services;

import com.rapifuzz.entities.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String emailId);

    Optional<User> findById(Long id);

    void save(User user);
}

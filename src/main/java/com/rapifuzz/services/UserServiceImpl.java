package com.rapifuzz.services;

import com.rapifuzz.dao.UserRepository;
import com.rapifuzz.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<User> findByEmail(String emailId) {
        return this.userRepository.findByEmail(emailId);
    }

    @Override
    public Optional<User> findById(Long id) {
        return this.userRepository.findById(id);
    }

    @Override
    public void save(User user) {
        this.userRepository.save(user);
    }
}

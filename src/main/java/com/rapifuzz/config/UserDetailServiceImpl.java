package com.rapifuzz.config;

import com.rapifuzz.dao.UserRepository;
import com.rapifuzz.entities.User;
import com.rapifuzz.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // fetching user from database
        Optional<User> userOptional = this.userService.findByEmail(username);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Could not found user !!");
        }
        return new CustomUserDetails(userOptional.get());
    }

}

package com.rapifuzz.controllers;

import com.rapifuzz.entities.PasswordResetToken;
import com.rapifuzz.entities.User;
import com.rapifuzz.entities.UserDto;
import com.rapifuzz.request_response.JwtRequest;
import com.rapifuzz.request_response.JwtResponse;
import com.rapifuzz.request_response.Mail;
import com.rapifuzz.security.JwtHelper;
import com.rapifuzz.services.MailService;
import com.rapifuzz.services.PasswordResetTokenService;
import com.rapifuzz.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtHelper helper;

    @Autowired
    private MailService mailService;

    @Autowired
    private PasswordResetTokenService tokenService;

    private Logger LOGGER = LoggerFactory.getLogger(AuthController.class);


    @GetMapping("/test")
    public ResponseEntity<String> getMessage() {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("method called ");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        this.doAuthenticate(request.getEmail(), request.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = this.helper.generateToken(userDetails);
        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .username(userDetails.getUsername()).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDto userDto) {
        if (userService.findByEmail(userDto.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists");
        }
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setAddress(userDto.getAddress());
        user.setCity(userDto.getCity());
        user.setPinCode(userDto.getPinCode());
        user.setCountry(userDto.getCountry());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setRole("USER");
        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<?> forgotPasswordForm(@RequestParam String emailId) {
        Optional<User> userOption = this.userService.findByEmail(emailId);
        if (userOption.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User doesn't exists");
        }
        String token = tokenService.createToken(userOption.get()).getToken();

        String resetUrl = "http://localhost:8080/api/auth/reset-password?token=" + token;
        Mail mail = generateMailWithBody(resetUrl, emailId);
        //send mail
        mailService.sendEmail(mail);
        return ResponseEntity.status(HttpStatus.OK).body("OTP send Successfully...");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        PasswordResetToken passwordResetToken = tokenService.validatePasswordResetToken(token);
        if (passwordResetToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
        }
        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.save(user);
        return ResponseEntity.status(HttpStatus.OK).body("Password reset successful");
    }

    private void doAuthenticate(String email, String password) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
        try {
            manager.authenticate(authentication);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(" Invalid Username or Password  !!");
        }
    }

    private Mail generateMailWithBody(String resetLink, String email) {
        Mail mail = new Mail();
        mail.setMailTo(email);
        mail.setMailSubject("Incident Password Reset Link");

        String htmlContent = "<html>" +
                "<body>" +
                "<h1>Welcome to Incident Management System</h1>" +
                "<p>Click the following link to reset your password:</p>" +
                "<p><a href=\"" + resetLink + "\">Reset Password</a></p>" +
                "</body>" +
                "</html>";

        mail.setMailContent(htmlContent);
        return mail;
    }


}

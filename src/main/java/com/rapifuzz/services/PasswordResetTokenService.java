package com.rapifuzz.services;

import com.rapifuzz.dao.PasswordResetTokenRepo;
import com.rapifuzz.entities.PasswordResetToken;
import com.rapifuzz.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetTokenService {

    @Autowired
    private PasswordResetTokenRepo passwordResetTokenRepo;

    public PasswordResetToken createToken(User user) {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryDate(new Date(System.currentTimeMillis() + 86400000));
        return passwordResetTokenRepo.save(token);
    }

    public PasswordResetToken validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> passToken = passwordResetTokenRepo.findByToken(token);
        if (passToken.isEmpty()) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        if (passToken.get().getExpiryDate().before(cal.getTime())) {
            return null;
        }
        return passToken.get();
    }
}

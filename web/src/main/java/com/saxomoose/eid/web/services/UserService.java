package com.saxomoose.eid.web.services;

import com.saxomoose.eid.web.exceptions.UniqueConstraintViolationException;
import com.saxomoose.eid.web.models.User;
import com.saxomoose.eid.web.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        final var user = userRepository.findByUsername(username);
        if (user == null) {
            return Optional.empty();
        }

        return Optional.of(user);
    }

    public void createNew(User user) throws UniqueConstraintViolationException {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            this.userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            if (e.getMostSpecificCause().getMessage().contains("username_unique_constraint")) {
                // TODO implement log and throw
                throw new UniqueConstraintViolationException();
            }
        }
    }

    public void activate(User user) {
        user.setEnabled(true);
        this.userRepository.save(user);
    }
}


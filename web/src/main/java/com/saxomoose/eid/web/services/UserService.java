package com.saxomoose.eid.web.services;

import com.saxomoose.eid.web.exceptions.UniqueConstraintViolationException;
import com.saxomoose.eid.web.models.User;
import com.saxomoose.eid.web.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService
{
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

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
        var test = passwordEncoder.encode(user.getPassword());


        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            this.userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            if (e.getMostSpecificCause().getMessage().contains("username_unique_constraint")) {
                logger.info(String.format("User not created. username %s already exists in eid.users.", user.getUsername()));
                throw new UniqueConstraintViolationException();
            }
        }
    }

    public void activate(User user) {
        user.setEnabled(true);
        this.userRepository.save(user);
    }
}


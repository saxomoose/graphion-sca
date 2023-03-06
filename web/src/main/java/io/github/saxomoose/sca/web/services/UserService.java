package io.github.saxomoose.sca.web.services;

import io.github.saxomoose.sca.web.dtos.User;
import io.github.saxomoose.sca.web.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.github.saxomoose.sca.web.exceptions.UniqueConstraintViolationException;

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

    public Optional<io.github.saxomoose.sca.web.models.User> findByUsername(String username) {
        final var user = userRepository.findByUsername(username);
        if (user == null) {
            return Optional.empty();
        }

        return Optional.of(user);
    }

    public void createNew(User userDTO) throws UniqueConstraintViolationException {

        final var password = passwordEncoder.encode(userDTO.getPassword());
        final var user = io.github.saxomoose.sca.web.models.User.of(userDTO.getUsername(), password);
        try {
            this.userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            if (e.getMostSpecificCause().getMessage().contains("username_unique_constraint")) {
                logger.info(String.format("User not created. username %s already exists in sca.users.", user.getUsername()));
                throw new UniqueConstraintViolationException();
            }
        }
    }

    public void activate(io.github.saxomoose.sca.web.models.User user) {
        user.setEnabled(Boolean.TRUE);
        this.userRepository.save(user);
    }
}


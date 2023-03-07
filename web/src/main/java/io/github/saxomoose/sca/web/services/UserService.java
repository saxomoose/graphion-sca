package io.github.saxomoose.sca.web.services;

import io.github.saxomoose.sca.web.exceptions.UniqueConstraintViolationException;
import io.github.saxomoose.sca.web.models.User;
import io.github.saxomoose.sca.web.models.projections.MutableUser;
import io.github.saxomoose.sca.web.repositories.UserRepository;
import io.github.saxomoose.sca.web.repositories.projections.MutableUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final MutableUserRepository mutableUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, MutableUserRepository mutableUserRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mutableUserRepository = mutableUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public void create(io.github.saxomoose.sca.web.dtos.User userDTO) throws UniqueConstraintViolationException {
        final var password = passwordEncoder.encode(userDTO.getPassword());
        var user = User.of(userDTO.getUsername(), password);
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            if (e.getMostSpecificCause().getMessage().contains("username_unique_constraint")) {
                logger.info(String.format("User not created. username %s already exists in sca.users.", user.getUsername()));
                throw new UniqueConstraintViolationException();
            }
        }
    }

    public void activate(User user) {
        final var mutableUser = MutableUser.of(user);
        mutableUser.setEnabled(Boolean.TRUE);
        mutableUserRepository.save(mutableUser);
    }
}


package io.github.saxomoose.sca.web.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.saxomoose.sca.web.models.User;
import io.github.saxomoose.sca.web.repositories.db.UserRepository;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User select(Long id)
    {
        return userRepository.findById(id).orElseThrow();
    }

    public User select(String username)
    {
        return userRepository.findByUsername(username);
    }
    //
    // public void create(io.github.saxomoose.sca.web.dtos.User userDTO) throws
    // DatabaseConstraintViolationException {
    // final var password = passwordEncoder.encode(userDTO.password());
    // var user = User.of(userDTO.username(), password);
    // try {
    // userRepository.save(user);
    // } catch (DataIntegrityViolationException exception) {
    // if
    // (exception.getMostSpecificCause().getMessage().contains(DatabaseConstraint.unique_constraint_user.name().toLowerCase()))
    // {
    // logger.info(String.format("User not created. username %s already exists in
    // sca.users.", user.getUsername()));
    // throw new
    // DatabaseConstraintViolationException(DatabaseConstraint.unique_constraint_user);
    // }
    // }
    // }
    //
    // public void activate(User user) {
    // user.setEnabled(Boolean.TRUE);
    // userRepository.save(user);
    // }
}

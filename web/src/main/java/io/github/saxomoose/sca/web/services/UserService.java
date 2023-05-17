package io.github.saxomoose.sca.web.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import io.github.saxomoose.sca.web.models.User;
import io.github.saxomoose.sca.web.services.repositories.UserRepository;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public User select(Long id)
    {
        return userRepository.findById(id).orElseThrow();
    }

    public User select(String username)
    {
        return userRepository.findByUsername(username);
    }

    public Page<User> usersByTenant(final Pageable pageable, final Long tenantId)
    {
        return PageableExecutionUtils.getPage(userRepository.usersByTenant(tenantId), pageable, () -> userRepository.countUsersByTenant(tenantId));
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

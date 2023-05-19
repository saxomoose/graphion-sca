package io.github.saxomoose.sca.web.services;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import io.github.saxomoose.sca.web.models.User;
import io.github.saxomoose.sca.web.services.repositories.UserRepository;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;
    private final UserRepository userRepository;

    @Autowired
    public UserService(NamedParameterJdbcOperations namedParameterJdbcOperations, UserRepository userRepository)
    {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
        this.userRepository = userRepository;
    }

    public User select(Long id)
    {
        return userRepository.findById(id).orElseThrow();
    }

    public User select(String username)
    {
        return userRepository.userByUsername(username);
    }

    public Page<User> usersByTenant(final Long tenantId, final Pageable pageable)
    {
        final Integer offset = pageable.getPageNumber() * pageable.getPageSize();
        final var sql = """
            SELECT *
            FROM sca.user
            WHERE id IN (
                SELECT "user"
                FROM sca.tenant_user
                WHERE tenant = :tenantId)
            LIMIT :pageSize OFFSET :offset;
        """;
        final var params = new MapSqlParameterSource(
            Map.of(
                "tenantId", tenantId,
                "pageSize", pageable.getPageSize(),
                "offset", offset));
        final List<User> tenantUsers = namedParameterJdbcOperations.queryForList(sql, params, User.class);
        return PageableExecutionUtils.getPage(tenantUsers, pageable, () -> userRepository.countUsersByTenant(tenantId));
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

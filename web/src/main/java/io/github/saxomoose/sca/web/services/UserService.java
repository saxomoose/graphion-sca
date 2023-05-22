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
import io.github.saxomoose.sca.web.config.db.RowMapperCollection;
import io.github.saxomoose.sca.web.models.User;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;
    private final RowMapperCollection rowMappers;


    @Autowired
    public UserService(NamedParameterJdbcOperations namedParameterJdbcOperations, RowMapperCollection rowMappers)
    {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
        this.rowMappers = rowMappers;
    }

    // Aggregate
    public Long countUsersByTenant(Long tenantId) {
        final var sql = """
        SELECT COUNT(*)
        FROM sca.user
        WHERE id IN (
            SELECT "user"
            FROM sca.tenant_user
            WHERE tenant = :tenantId); 
        """;
        final var params = new MapSqlParameterSource().addValue("tenantId", tenantId);
        return namedParameterJdbcOperations.queryForObject(sql, params, Long.class);
    }

    // Read
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
        final List<User> tenantUsers = namedParameterJdbcOperations.query(sql, params, rowMappers.getRowMapper(User.class));
        return PageableExecutionUtils.getPage(tenantUsers, pageable, () -> countUsersByTenant(tenantId));
    }

    public User firstUser()
    {
        final var sql = """
        SELECT * FROM sca.user LIMIT 1;    
        """;
        return namedParameterJdbcOperations.getJdbcOperations().queryForObject(sql, rowMappers.getRowMapper(User.class));
    }

    public User userById(Long userId)
    {
        final var sql = """
        SELECT * FROM sca.user WHERE id = :userId;    
        """;
        final var params = new MapSqlParameterSource().addValue("userId", userId);
        return namedParameterJdbcOperations.queryForObject(sql, params, rowMappers.getRowMapper(User.class));
    }

    public User userByUsername(String username)
    {
        final var sql = """
        SELECT * FROM sca.user WHERE username = :username;    
        """;
        final var params = new MapSqlParameterSource().addValue("username", username);
        return namedParameterJdbcOperations.queryForObject(sql, params, rowMappers.getRowMapper(User.class));
    }

    // Update
    public User insert(User user, Long principalId)
    {
        final var sql = """
        INSERT INTO sca.user
        VALUES (DEFAULT, DEFAULT, :firstName, :lastName, :username, :dateOfBirth, DEFAULT, :createdBy, :lastUpdatedBy, DEFAULT, DEFAULT)
        RETURNING *;        
        """;
        final var params = new MapSqlParameterSource(
            Map.of(
                "firstName", user.firstName(),
                "lastName", user.lastName(),
                "username", user.username(),
                "dateOfBirth", user.dateOfBirth(),
                "createdBy", principalId,
                "lastUpdatedBy", principalId));
        return namedParameterJdbcOperations.queryForObject(sql, params, rowMappers.getRowMapper(User.class));
    }
}

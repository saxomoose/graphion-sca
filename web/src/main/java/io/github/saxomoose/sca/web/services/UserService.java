package io.github.saxomoose.sca.web.services;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import io.github.saxomoose.sca.web.config.db.RowMapperCollection;
import io.github.saxomoose.sca.web.models.User;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;
    private final RowMapper<User> rowMapper;

    @Autowired
    public UserService(NamedParameterJdbcOperations namedParameterJdbcOperations, RowMapperCollection rowMappers)
    {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
        this.rowMapper = (RowMapper<User>) rowMappers.getRowMapper(User.class);
    }

    // Data
    // Aggregate
    public Long countUsersByTenant(final Long tenantId) {
        final String sql = """
        SELECT COUNT(*)
        FROM sca.user
        WHERE id IN (
            SELECT "user"
            FROM sca.tenant_user
            WHERE tenant = :tenantId); 
        """;
        final MapSqlParameterSource params = new MapSqlParameterSource().addValue("tenantId", tenantId);
        return namedParameterJdbcOperations.queryForObject(sql, params, Long.class);
    }

    // Read
    public List<User> usersByIds(final List<Long> userIds)
    {
        final String sql = """
            SELECT *
            FROM sca.user
            WHERE id IN (:userIds);    
        """;
        final MapSqlParameterSource params = new MapSqlParameterSource(Map.of("userIds", userIds));
        return namedParameterJdbcOperations.query(sql, params, rowMapper);
    }

    public Page<User> usersByTenant(final Long tenantId, final Pageable pageable)
    {
        final Integer offset = pageable.getPageNumber() * pageable.getPageSize();
        final String sql = """
            SELECT *
            FROM sca.user
            WHERE id IN (
                SELECT "user"
                FROM sca.tenant_user
                WHERE tenant = :tenantId)
            ORDER BY last_name
            LIMIT :pageSize OFFSET :offset;
        """;
        final MapSqlParameterSource params = new MapSqlParameterSource(
            Map.of(
                "tenantId", tenantId,
                "pageSize", pageable.getPageSize(),
                "offset", offset));
        final List<User> tenantUsers = namedParameterJdbcOperations.query(sql, params, rowMapper);
        return PageableExecutionUtils.getPage(tenantUsers, pageable, () -> countUsersByTenant(tenantId));
    }

    public User firstUser()
    {
        final String sql = """
        SELECT * FROM sca.user LIMIT 1;    
        """;
        return namedParameterJdbcOperations.getJdbcOperations().queryForObject(sql, rowMapper);
    }

    public User userById(final Long userId)
    {
        final String sql = """
        SELECT * FROM sca.user WHERE id = :userId;    
        """;
        final MapSqlParameterSource params = new MapSqlParameterSource().addValue("userId", userId);
        return namedParameterJdbcOperations.queryForObject(sql, params, rowMapper);
    }

    public User userByUsername(final String username)
    {
        final String sql = """
        SELECT * FROM sca.user WHERE username = :username;    
        """;
        final MapSqlParameterSource params = new MapSqlParameterSource().addValue("username", username);
        return namedParameterJdbcOperations.queryForObject(sql, params, rowMapper);
    }

    // Update
    public User insert(final User user, final Long principalId)
    {
        final String sql = """
        INSERT INTO sca.user
        VALUES (DEFAULT, :username, NULL, :firstName, :lastName, :dateOfBirth, DEFAULT, DEFAULT, :createdBy, :lastUpdatedBy, DEFAULT, DEFAULT)
        RETURNING *;        
        """;
        final MapSqlParameterSource params = new MapSqlParameterSource(
            Map.of(
                "username", user.username(),
                "firstName", user.firstName(),
                "lastName", user.lastName(),
                "dateOfBirth", user.dateOfBirth(),
                "createdBy", principalId,
                "lastUpdatedBy", principalId));
        return namedParameterJdbcOperations.queryForObject(sql, params, rowMapper);
    }
}

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
    public Long countUsersByTenant(Long tenantId) {
        String sql = """
        SELECT COUNT(*)
        FROM sca.user
        WHERE id IN (
            SELECT "user"
            FROM sca.tenant_user
            WHERE tenant = :tenantId); 
        """;
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("tenantId", tenantId);
        return namedParameterJdbcOperations.queryForObject(sql, params, Long.class);
    }

    // Read
    public List<User> usersByIds(List<Long> userIds)
    {
        String sql = """
            SELECT *
            FROM sca.user
            WHERE id IN (:userIds);    
        """;
        MapSqlParameterSource params = new MapSqlParameterSource(Map.of("userIds", userIds));
        return namedParameterJdbcOperations.query(sql, params, rowMapper);
    }

    public Page<User> usersByTenant(Long tenantId, Pageable pageable)
    {
        Integer offset = pageable.getPageNumber() * pageable.getPageSize();
        String sql = """
            SELECT *
            FROM sca.user
            WHERE id IN (
                SELECT "user"
                FROM sca.tenant_user
                WHERE tenant = :tenantId)
            ORDER BY last_name
            LIMIT :pageSize OFFSET :offset;
        """;
        MapSqlParameterSource params = new MapSqlParameterSource(
            Map.of(
                "tenantId", tenantId,
                "pageSize", pageable.getPageSize(),
                "offset", offset));
        List<User> tenantUsers = namedParameterJdbcOperations.query(sql, params, rowMapper);
        return PageableExecutionUtils.getPage(tenantUsers, pageable, () -> countUsersByTenant(tenantId));
    }

    public User firstUser()
    {
        String sql = """
        SELECT * FROM sca.user LIMIT 1;    
        """;
        return namedParameterJdbcOperations.getJdbcOperations().queryForObject(sql, rowMapper);
    }

    public User userById(Long userId)
    {
        String sql = """
        SELECT * FROM sca.user WHERE id = :userId;    
        """;
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("userId", userId);
        return namedParameterJdbcOperations.queryForObject(sql, params, rowMapper);
    }

    public User userByUsername(String username)
    {
        String sql = """
        SELECT * FROM sca.user WHERE username = :username;    
        """;
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("username", username);
        return namedParameterJdbcOperations.queryForObject(sql, params, rowMapper);
    }

    // Update
    public User insert(User user, Long principalId)
    {
        String sql = """
        INSERT INTO sca.user
        VALUES (DEFAULT, :username, NULL, :firstName, :lastName, :dateOfBirth, DEFAULT, DEFAULT, :createdBy, :lastUpdatedBy, DEFAULT, DEFAULT)
        RETURNING *;        
        """;
        MapSqlParameterSource params = new MapSqlParameterSource(
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

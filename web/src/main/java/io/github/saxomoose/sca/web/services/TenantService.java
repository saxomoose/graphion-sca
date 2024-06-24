package io.github.saxomoose.sca.web.services;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import io.github.saxomoose.sca.web.config.db.RowMapperCollection;
import io.github.saxomoose.sca.web.enums.RoleEnum;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.utils.Tuple;

@Service
public class TenantService {
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;
    private final RowMapperCollection rowMappers;

    @Autowired
    public TenantService(NamedParameterJdbcOperations namedParameterJdbcOperations, RowMapperCollection rowMappers) 
    {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
        this.rowMappers = rowMappers;

    }

    // Data
    // Aggregate
    public Long count() {
        String sql = """
        SELECT COUNT(*)
        FROM sca.tenant;
        """;
        return namedParameterJdbcOperations.getJdbcOperations().queryForObject(sql, Long.class);
    }

    public Long countTenantsByUser(Long userId) 
    {
        String sql = """
        SELECT COUNT(*)
        FROM sca.tenant
        WHERE id IN (
            SELECT tenant
            FROM sca.tenant_user
            WHERE user = userId);    
        """;
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("userId", userId);
        return namedParameterJdbcOperations.queryForObject(sql, params, Long.class);
    }

    // Read
    public Page<Tenant> tenantsByUser(Long userId, Pageable pageable)
    {
        Integer offset = pageable.getPageNumber() * pageable.getPageSize();
        String sql = """
            SELECT *
            FROM sca.tenant
            WHERE id IN (
                SELECT tenant
                FROM sca.tenant_user
                WHERE "user" = :userId)
            LIMIT 10 OFFSET 0;
        """;
        MapSqlParameterSource params = new MapSqlParameterSource(
            Map.of(
                "userId", userId,
                "pageSize", pageable.getPageSize(),
                "offset", offset));
        List<Tenant> userTenants = 
            namedParameterJdbcOperations.query(sql, params, rowMappers.getRowMapper(Tenant.class));
        return PageableExecutionUtils.getPage(
            userTenants,
            pageable,
            () -> countTenantsByUser(userId));
    }

    public Tenant firstTenant() {
        String sql = """
            SELECT * FROM sca.tenant LIMIT 1;    
        """;
        return namedParameterJdbcOperations.getJdbcOperations().queryForObject(sql, rowMappers.getRowMapper(Tenant.class));
    }

    public Tenant tenantById(Long tenantId)
    {
        String sql = """
            SELECT * FROM sca.tenant WHERE id = :tenantId;    
        """;
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("tenantId", tenantId);
        return namedParameterJdbcOperations.queryForObject(sql, params, rowMappers.getRowMapper(Tenant.class));
    }

    public Tenant tenantByName(String tenantName)
    {
        String sql = """
            SELECT * FROM sca.tenant WHERE name = :tenantName;    
        """;
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("tenantName", tenantName);
        return namedParameterJdbcOperations.queryForObject(sql, params, rowMappers.getRowMapper(Tenant.class));
    }

    public Tuple<Tenant, List<GrantedAuthority>> tenantWithUserAuthority(Long userId, Boolean defaultConnection)
    {
        String sql = """
            SELECT t.*, tu.role AS role
            FROM sca.tenant t
            JOIN sca.tenant_user tu ON t.id = tu.tenant
            WHERE tu."user" = :userId AND tu.default_connection = :defaultConnection 
        """;

        MapSqlParameterSource params = new MapSqlParameterSource(
            Map.of(
                "userId", userId,
                "defaultConnection", defaultConnection)
        );

        try {
            return namedParameterJdbcOperations.queryForObject(
                sql, 
                params, 
                (rs, rowNum) -> {
                    return new Tuple<Tenant, List<GrantedAuthority>>(
                        new Tenant(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("bucket")),
                        List.of(new SimpleGrantedAuthority(rs.getString("role"))));
                    });
        } catch(EmptyResultDataAccessException exception) {
            return null;
        }
    }

    // Update
    public Tenant insert(Tenant tenant, Long principalId)
    {
        String sql = """
        INSERT INTO sca.tenant
        VALUES (DEFAULT, :name, :bucket, :createdBy, :lastUpdatedBy, DEFAULT, DEFAULT)
        RETURNING *;        
        """;
        MapSqlParameterSource params = new MapSqlParameterSource(
            Map.of(
                "name", tenant.name(),
                "bucket", tenant.bucket(),
                "createdBy", principalId,
                "lastUpdatedBy", principalId));
        return namedParameterJdbcOperations.queryForObject(sql, params, rowMappers.getRowMapper(Tenant.class));
    }

    public void insertRole(Long tenantId, Long userId, RoleEnum role) 
    {
        String sql = """
            INSERT INTO sca.tenant_user
            VALUES (:tenantId, :userId, :role, DEFAULT);
        """;
        MapSqlParameterSource params = new MapSqlParameterSource().addValues(
            Map.of(
                "tenantId", tenantId,
                "userId", userId,
                "role", role.toString()));

        namedParameterJdbcOperations.update(sql, params);
    }

    public void setDefaultConnection(Long tenantId, Long userId) 
    {
        String sql = """
            UPDATE sca.tenant_user
            SET default_connection = 
                CASE 
                    WHEN tenant = :tenantId AND "user" = :userId THEN true
                    ELSE false
                END
            WHERE tenant = :tenantId AND "user" = :userId;
        """;
        MapSqlParameterSource params = new MapSqlParameterSource().addValues(
            Map.of(
                "tenantId", tenantId,
                "userId", userId));

        namedParameterJdbcOperations.update(sql, params);
    }

}

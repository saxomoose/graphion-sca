package io.github.saxomoose.sca.web.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import io.github.saxomoose.sca.web.enums.Role;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.models.TenantRowMapper;
import io.github.saxomoose.sca.web.services.repositories.TenantRepository;
import io.github.saxomoose.sca.web.utils.Tuple;

@Service
public class TenantService {
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;
    private final TenantRepository tenantRepository;

    @Autowired
    public TenantService(NamedParameterJdbcOperations namedParameterJdbcOperations, TenantRepository tenantRepository) 
    {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
        this.tenantRepository = tenantRepository;
    }

    // Read
    public Tenant tenant(Long id) 
    {
        return tenantRepository.tenantById(id);
    }

    public Page<Tenant> tenantsByUser(UUID userUUID, Pageable pageable)
    {
        final Integer offset = pageable.getPageNumber() * pageable.getPageSize();
        final var sql = """
            SELECT *
            FROM sca.tenant
            WHERE id IN (
                SELECT tenant
                FROM sca.tenant_user
                WHERE "user" = (
                    SELECT id
                    FROM sca.user
                    WHERE uuid = :userUUID ))
            LIMIT 10 OFFSET 0;
        """;
        final var params = new MapSqlParameterSource(
            Map.of(
                "userUUID", userUUID,
                "pageSize", pageable.getPageSize(),
                "offset", offset));
        final List<Tenant> userTenants = 
            namedParameterJdbcOperations.query(sql, params, new TenantRowMapper());
        return PageableExecutionUtils.getPage(
            userTenants,
            pageable,
            () -> tenantRepository.countTenantsByUser(userUUID));
    }

    public Tuple<Tenant, List<GrantedAuthority>> tenantWithUserAuthority(Long userId, Boolean defaultConnection)
    {
        final var sql = """
            SELECT t.*, tu.role AS role
            FROM sca.tenant t
            JOIN sca.tenant_user tu ON t.id = tu.tenant
            WHERE tu."user" = :userId AND tu.default_connection = :defaultConnection 
        """;

        final var params = new MapSqlParameterSource(
            Map.of(
                "userId", userId,
                "defaultConnection", defaultConnection)
        );

        try {
            return namedParameterJdbcOperations.queryForObject(sql, params, (rs, rowNum) -> {
            return Tuple.of(
                new Tenant(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("bucket"),
                    rs.getLong("created_by"),
                    rs.getLong("last_modified_by"),
                    rs.getTimestamp("created_date").toLocalDateTime(),
                    rs.getTimestamp("last_modified_date").toLocalDateTime()),
                List.of(new SimpleGrantedAuthority(rs.getString("role"))));
            });
        } catch(EmptyResultDataAccessException exception) {
            return null;
        }
    }

    // Create
    public void upsertTenantUser(Long tenantId, Long userId, Role role, Boolean defaultConnection) 
    {
        final var sql = """
            INSERT INTO sca.tenant_user (tenant, "user", role, default_connection)
            VALUES (:tenantId, :userId, :role, :defaultConnection)
            ON CONFLICT (tenant, "user") DO UPDATE
            SET role = :role, default_connection = :defaultConnection
        """;
        final var params = new MapSqlParameterSource().addValues(
            Map.of(
                "tenantId", tenantId,
                "userId", userId,
                "role", role.toString(),
                "defaultConnection", defaultConnection));

        namedParameterJdbcOperations.update(sql, params);
    }

    // Update
    public void setDefaultTenant(Long tenantId, Long userId) 
    {
        final var sql = """
            UPDATE sca.tenant_user
            SET default_connection = 
                CASE 
                    WHEN tenant = :tenantId AND "user" = :userId THEN true
                    ELSE false
                END
            WHERE tenant = :tenantId AND "user" = :userId;
        """;
        final var params = new MapSqlParameterSource().addValues(
            Map.of(
                "tenantId", tenantId,
                "userId", userId));

        namedParameterJdbcOperations.update(sql, params);
    }
}

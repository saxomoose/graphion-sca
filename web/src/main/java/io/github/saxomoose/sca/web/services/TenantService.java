package io.github.saxomoose.sca.web.services;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import io.github.saxomoose.sca.web.enums.Role;
import io.github.saxomoose.sca.web.models.Tenant;
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

    public Page<Tenant> tenantsByUser(Long userId)
    {
        return tenantRepository.tenantsByUserId(userId);
    }

    public Tuple<Tenant, Role> defaultTenant(Long userId)
    {
        final var sql = """
            SELECT t.*, tu.role AS role
            FROM sca.tenant t
            JOIN sca.tenant_user tu ON t.id = tu.tenant
            WHERE tu."user" = 1 AND tu.default_connection = true
        """;

        final var params = new MapSqlParameterSource(
            Map.of("userId", userId)
        );

        try {
            return namedParameterJdbcOperations.queryForObject(sql, params, (rs, rowNum) -> {
            return Tuple.of(
                new Tenant(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("bucket")),
                Role.valueOf(rs.getString("role")));
            });
        } catch(EmptyResultDataAccessException exception) {
            return null;
        }
    }

    // Create
    public void upsertTenantUser(Long tenantId, Long userId, Role role, Boolean defaultConnection) {
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


}

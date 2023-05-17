package io.github.saxomoose.sca.web.services;

import java.util.Collection;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Collection<Tenant> tenantsByUser(Long userId)
    {
        return tenantRepository.tenantsByUserId(userId);
    }

    public Tuple<Tenant, Role> defaultTenant(Long userId)
    {
        final var rawDefaultRole = tenantRepository.defaultRole(userId);
        if(rawDefaultRole.isPresent()) {
            final var defaultTenant = tenantRepository.tenantById(rawDefaultRole.get().first());
            final var defaultRole = Role.valueOf(rawDefaultRole.get().second());
            return Tuple.of(defaultTenant, defaultRole);
        } else {
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

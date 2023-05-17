package io.github.saxomoose.sca.web.services.repositories;

import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;
import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.utils.Tuple;

public interface TenantRepository extends CrudRepository<Tenant, Long> {
    @Query("SELECT *  FROM sca.tenant LIMIT 1")
    Tenant fistTenant();

    @Query("SELECT * FROM sca.tenant WHERE id = :id")
    Tenant tenantById(Long id);

    @Query("""
        SELECT *
        FROM sca.tenant
        WHERE id IN (
            SELECT tenant
            FROM sca.tenant_user
            WHERE "user" = :userId
        );
    """)
    Collection<Tenant> tenantsByUserId(Long userId);

    @Nullable
    @Query("""
        SELECT tenant AS first, role AS second
        FROM sca.tenant_user
        WHERE "user" = :userId AND default_connection = true;
    """)
    Optional<Tuple<Long, String>> defaultRole(Long userId);
}

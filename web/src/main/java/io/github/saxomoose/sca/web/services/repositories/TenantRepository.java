package io.github.saxomoose.sca.web.services.repositories;

import java.util.UUID;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import io.github.saxomoose.sca.web.models.Tenant;

public interface TenantRepository extends CrudRepository<Tenant, Long> {
    // Read
    @Query(value = """
        SELECT COUNT(*)
        FROM sca.tenant
        WHERE id IN (
            SELECT tenant
            FROM sca.tenant_user
            WHERE user = (
                SELECT id
                FROM sca.user
                WHERE uuid = :uuid ));        
    """)
    Long countTenantsByUser(UUID userUUID);

    @Query("SELECT *  FROM sca.tenant LIMIT 1")
    Tenant fistTenant();

    @Query("SELECT * FROM sca.tenant WHERE id = :id")
    Tenant tenantById(Long id);
}

package io.github.saxomoose.sca.web.services.repositories;

import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import io.github.saxomoose.sca.web.models.Tenant;

public interface TenantRepository extends CrudRepository<Tenant, Long> {
    // Read
    @Query("""
        SELECT *
        FROM sca.tenant
        WHERE id IN (
            SELECT tenant
            FROM sca.tenant_user
            WHERE "user" = :userId
        );
    """)
    List<Tenant> tenantsByUserId(Long userId);

    @Query(value = """
        SELECT COUNT(*)
        FROM sca.tenant
        WHERE id IN (
            SELECT tenant
            FROM sca.tenant_user
            WHERE  = :tenantId);        
    """)
    Long countUsersByTenant(Long tenantId);

    @Query("SELECT *  FROM sca.tenant LIMIT 1")
    Tenant fistTenant();

    @Query("SELECT * FROM sca.tenant WHERE id = :id")
    Tenant tenantById(Long id);
}

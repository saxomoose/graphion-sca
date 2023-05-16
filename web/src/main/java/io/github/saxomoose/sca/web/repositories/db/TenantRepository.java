package io.github.saxomoose.sca.web.repositories.db;

import java.util.Collection;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import io.github.saxomoose.sca.web.models.Tenant;

public interface TenantRepository extends CrudRepository<Tenant, Long> {
    @Query("SELECT *  FROM sca.tenant LIMIT 1")
    Tenant selectFirst();

    @Query("SELECT * FROM sca.tenant WHERE id = :id")
    Tenant selectById(Long id);

    @Query("""
        SELECT *
        FROM sca.tenant
        WHERE id IN (
            SELECT tenant
            FROM sca.tenant_user
            WHERE "user" = :userId
        );
    """)
    Collection<Tenant> selectByUserId(Long userId);

    // @Query("""
    //         SELECT *
    //         FROM sca.tenant
    //         WHERE id = (
    //             SELECT tenant
    //             FROM sca.user
    //             WHERE username = :username
    //         )
    //         """)
    // Tenant select(String username);
}

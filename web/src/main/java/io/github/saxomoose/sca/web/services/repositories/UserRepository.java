package io.github.saxomoose.sca.web.services.repositories;

import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import io.github.saxomoose.sca.web.models.User;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);

    @Query("SELECT * FROM sca.user LIMIT 1")
    User select();

    @Query(value = """
        SELECT *
        FROM sca.user
        WHERE id IN (
            SELECT "user"
            FROM sca.tenant_user
            WHERE tenant = :tenantId);        
    """)
    List<User> usersByTenant(Long tenantId);

    @Query(value = """
        SELECT COUNT(*)
        FROM sca.user
        WHERE id IN (
            SELECT "user"
            FROM sca.tenant_user
            WHERE tenant = :tenantId);        
    """)
    Long countUsersByTenant(Long tenantId);
}

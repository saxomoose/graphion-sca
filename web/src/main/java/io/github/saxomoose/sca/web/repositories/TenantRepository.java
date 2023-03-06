package io.github.saxomoose.sca.web.repositories;

import io.github.saxomoose.sca.web.models.Tenant;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface TenantRepository extends CrudRepository<Tenant, Long> {
    @Query("SELECT *  FROM sca.tenant LIMIT 1")
    Tenant findFirst();
}

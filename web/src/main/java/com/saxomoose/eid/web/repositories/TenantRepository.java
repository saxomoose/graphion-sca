package com.saxomoose.eid.web.repositories;

import com.saxomoose.eid.web.models.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    @Query(nativeQuery = true, value="SELECT *  FROM eid.tenants ORDER BY random() LIMIT 1")
    Tenant findRandom();
}

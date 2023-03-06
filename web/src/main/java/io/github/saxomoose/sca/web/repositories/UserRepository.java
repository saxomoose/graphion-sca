package io.github.saxomoose.sca.web.repositories;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import io.github.saxomoose.sca.web.models.User;

import java.util.Collection;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);

    @Query("SELECT *  FROM sca.user LIMIT 1")
    User findFirst();

    @Query("SELECT * FROM sca.user WHERE tenant = :id")
    Collection<User> findByTenant(Long tenantId);
}

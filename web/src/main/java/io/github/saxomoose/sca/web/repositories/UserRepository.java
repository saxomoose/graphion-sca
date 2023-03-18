package io.github.saxomoose.sca.web.repositories;

import io.github.saxomoose.sca.web.models.User;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);

    @Query("SELECT *  FROM sca.user LIMIT 1")
    User select();

    Collection<User> findAllByTenant(Long tenantId);
}

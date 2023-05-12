package io.github.saxomoose.sca.web.repositories.db;

import java.util.Collection;
import java.util.UUID;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import io.github.saxomoose.sca.web.models.User;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);

    @Query("SELECT * FROM sca.user LIMIT 1")
    User select();

    Collection<User> findAllByTenant(Long tenantId);

    @Query("SELECT * FROM sca.user WHERE uuid = :uuid")
    User select(UUID uuid);
}

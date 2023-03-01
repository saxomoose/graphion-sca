package sca.web.repositories;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import sca.web.models.User;

import java.util.Collection;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);

    @Query("SELECT *  FROM eid.user LIMIT 1")
    User findFirst();

    @Query("SELECT * FROM eid.user WHERE tenant = :id")
    Collection<User> findByTenant(Long tenantId);
}

package com.saxomoose.eid.web.repositories;

import com.saxomoose.eid.web.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    @Query(nativeQuery = true, value="SELECT *  FROM eid.users LIMIT 1")
    User findFirst();
}

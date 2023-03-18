package io.github.saxomoose.sca.web.repositories;

import io.github.saxomoose.sca.web.models.Signatory;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface SignatoryRepository extends CrudRepository<Signatory, Long> {
    @Query("SELECT *  FROM sca.signatory LIMIT 1")
    Signatory select();
}

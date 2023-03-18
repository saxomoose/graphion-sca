package io.github.saxomoose.sca.web.repositories;

import io.github.saxomoose.sca.web.models.LegalPerson;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface LegalPersonRepository extends CrudRepository<LegalPerson, Long> {
    @Query("SELECT *  FROM sca.legal_person LIMIT 1")
    LegalPerson select();
}

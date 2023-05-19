package io.github.saxomoose.sca.web.services.repositories;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import io.github.saxomoose.sca.web.models.LegalPerson;

public interface LegalPersonRepository extends CrudRepository<LegalPerson, Long> {
    // Read
    @Query("SELECT *  FROM sca.legal_person LIMIT 1")
    LegalPerson firstLegalPerson();
}

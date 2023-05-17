package io.github.saxomoose.sca.web.services.repositories;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import io.github.saxomoose.sca.web.models.LegalPerson;

public interface LegalPersonRepository extends CrudRepository<LegalPerson, Long>, PagingAndSortingRepository<LegalPerson, Long> {
    @Query("SELECT *  FROM sca.legal_person LIMIT 1")
    LegalPerson select();
}

package io.github.saxomoose.sca.web.repositories.db;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import io.github.saxomoose.sca.web.models.Signatory;

public interface SignatoryRepository extends CrudRepository<Signatory, Long> {
    @Query("SELECT *  FROM sca.signatory LIMIT 1")
    Signatory select();

    @Query("SELECT * FROM sca.signatory WHERE national_registration_number = :nationalRegistrationNumber")
    Signatory select(Long nationalRegistrationNumber);
}

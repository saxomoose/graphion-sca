package sca.web.repositories;

import sca.web.models.Closing;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface ClosingRepository extends CrudRepository<Closing, Long> {
    @Query("SELECT *  FROM eid.closing LIMIT 1")
    Closing findFirst();
}

package sca.web.repositories;

import org.springframework.data.repository.CrudRepository;
import sca.web.models.LegalPerson;

public interface LegalPersonRepository extends CrudRepository<LegalPerson, Long> {
}

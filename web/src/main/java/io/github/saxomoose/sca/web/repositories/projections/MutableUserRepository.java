package io.github.saxomoose.sca.web.repositories.projections;

import io.github.saxomoose.sca.web.models.projections.MutableUser;
import org.springframework.data.repository.CrudRepository;

public interface MutableUserRepository extends CrudRepository<MutableUser, Long> {
}

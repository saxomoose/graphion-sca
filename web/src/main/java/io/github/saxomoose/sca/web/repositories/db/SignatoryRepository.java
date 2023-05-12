package io.github.saxomoose.sca.web.repositories.db;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import io.github.saxomoose.sca.web.models.Signatory;

public interface SignatoryRepository extends CrudRepository<Signatory, Long> {
    
    Page<Signatory> findAllByTenant(Pageable pageable, Long tenantId);

    @Query("SELECT *  FROM sca.signatory LIMIT 1")
    Signatory select();

    @Query("SELECT * FROM sca.signatory WHERE uuid = :uuid")
    Signatory select(UUID uuid);

    
}

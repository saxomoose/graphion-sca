package com.saxomoose.eid.web.repositories;

import com.saxomoose.eid.web.models.Signatory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignatoryRepository extends JpaRepository<Signatory, Long> {
}

package com.saxomoose.eid.web.repositories;

import com.saxomoose.eid.web.models.Signatory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignatoryRepository extends JpaRepository<Signatory, Long> {
}

package com.saxomoose.eid.web.repositories;

import com.saxomoose.eid.web.models.ClosingSignatory;
import com.saxomoose.eid.web.models.ClosingSignatoryKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClosingSignatoryRepository extends JpaRepository<ClosingSignatory, ClosingSignatoryKey> {
}

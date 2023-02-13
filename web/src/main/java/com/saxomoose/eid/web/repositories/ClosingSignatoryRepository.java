package com.saxomoose.eid.web.repositories;

import com.saxomoose.eid.web.models.ClosingSignatory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClosingSignatoryRepository extends JpaRepository<ClosingSignatory, Long> {
}

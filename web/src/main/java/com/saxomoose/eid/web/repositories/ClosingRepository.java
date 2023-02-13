package com.saxomoose.eid.web.repositories;

import com.saxomoose.eid.web.models.Closing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClosingRepository extends JpaRepository<Closing, Long> {

}

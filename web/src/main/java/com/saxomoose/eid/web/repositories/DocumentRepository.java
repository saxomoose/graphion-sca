package com.saxomoose.eid.web.repositories;

import com.saxomoose.eid.web.models.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}

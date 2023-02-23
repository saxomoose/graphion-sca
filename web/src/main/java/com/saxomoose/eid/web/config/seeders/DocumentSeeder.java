package com.saxomoose.eid.web.config.seeders;

import com.saxomoose.eid.web.models.Document;
import com.saxomoose.eid.web.repositories.ClosingRepository;
import com.saxomoose.eid.web.repositories.DocumentRepository;
import com.saxomoose.eid.web.repositories.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(4)
public class DocumentSeeder implements CommandLineRunner {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private ClosingRepository closingRepository;

    @Override
    public void run(String... args) throws Exception {
        load();
    }

    private void load() {
        if (documentRepository.count() == 0) {
            var tenant = tenantRepository.findRandom();
            var closing = closingRepository.findRandom();
            var document = new Document(tenant, closing, "demo_document_1");
            documentRepository.save(document);
        }
    }
}

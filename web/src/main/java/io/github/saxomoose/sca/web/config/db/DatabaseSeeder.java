package io.github.saxomoose.sca.web.config.db;

import io.github.saxomoose.sca.web.enums.Function;
import io.github.saxomoose.sca.web.models.*;
import io.github.saxomoose.sca.web.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class DatabaseSeeder implements CommandLineRunner {
    @Autowired
    private Environment environment;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SignatoryRepository signatoryRepository;
    @Autowired
    private ClosingRepository closingRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private LegalPersonRepository legalPersonRepository;

    @Override
    public void run(String... args) {
        load();
    }

    private void load() {
        if (tenantRepository.count() == 0) {
            var tenant = Tenant.of("sca");
            tenantRepository.save(tenant);
            var tenant1 = tenantRepository.findFirst();

            var user = User.of(environment.getProperty("seeder.adminusername"), environment.getProperty("seeder.adminpassword"));
            userRepository.save(user);
            var user1 = userRepository.findFirst();

//            var mutableUser = MutableUser.of(user1);
//            mutableUser.setEnabled(Boolean.FALSE);
//            mutableUserRepository.save(mutableUser);

            var signatory = Signatory.of(26854545232L, "Evarest", "Schoofs", "evarest@northstone.test", tenant1.getId());
            signatoryRepository.save(signatory);
            var signatory1 = signatoryRepository.findFirst();

            // signatoryRepository.delete(signatory1);

            var document = Document.of("demo_document_1", "/documents/", tenant1.getId());
            documentRepository.save(document);
            var document1 = documentRepository.findFirst();

            var closing = Closing.of("demo_closing_1", ZoneId.of("Europe/Brussels"), LocalDateTime.now(), tenant1.getId());
            closing.addNaturalPersonSignatory(signatory1.getId(), document1.getId());
            closingRepository.save(closing);
            var closing1 = closingRepository.findFirst();

            closingRepository.setSignatureTimestamp(closing1.getId(), signatory1.getId(), Instant.now());
            closingRepository.save(closing1);

            var legalPerson = LegalPerson.of(8254565954L, tenant1.getId());
            legalPerson.addOfficer(signatory1.getId(), Function.DIRECTOR, null);
            legalPersonRepository.save(legalPerson);
            var legalPerson1 = legalPersonRepository.findFirst();

            closing1.addLegalPersonSignatory(legalPerson1.getId());
            closingRepository.save(closing1);

            var firstClosingUpdated = closingRepository.findFirst();

        }
    }
}

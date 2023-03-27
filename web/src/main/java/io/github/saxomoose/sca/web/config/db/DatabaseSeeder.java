package io.github.saxomoose.sca.web.config.db;

import io.github.saxomoose.sca.web.enums.Function;
import io.github.saxomoose.sca.web.models.*;
import io.github.saxomoose.sca.web.repositories.db.*;
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
            tenant = tenantRepository.select();

            var user = User.of(environment.getProperty("seeder.adminusername"), environment.getProperty("seeder.adminpassword"));
            userRepository.save(user);
            user = userRepository.select();

//            var mutableUser = MutableUser.of(user);
//            mutableUser.setEnabled(Boolean.FALSE);
//            mutableUserRepository.save(mutableUser);

            var signatory = Signatory.of(tenant.id(), 1111111111L, "FirstName", "LastName", "firstname.lastname@sca.test");
            signatoryRepository.save(signatory);
            signatory = signatoryRepository.select();

            // signatoryRepository.delete(signatory);

            var document = Document.of(tenant.id(), "demo_document_1", "/documents/");
            documentRepository.save(document);
            document = documentRepository.select();

            var closing = Closing.of(tenant.id(), "demo_closing_1", ZoneId.of("Europe/Brussels"), LocalDateTime.now());
            closing.addNaturalPersonSignatory(signatory.id(), document.id());
            closingRepository.save(closing);
            closing = closingRepository.select();

            closingRepository.update(closing.id(), signatory.id(), Instant.now());
            closingRepository.save(closing);

            var legalPerson = LegalPerson.of(tenant.id(), 1111111111L);
            legalPerson.addOfficer(signatory.id(), Function.DIRECTOR, null);
            legalPersonRepository.save(legalPerson);
            legalPerson = legalPersonRepository.select();

            closing.addLegalPersonSignatory(legalPerson.id());
            closingRepository.save(closing);

            var updatedClosing = closingRepository.select();
        }
    }
}

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
            tenant = tenantRepository.findFirst();

            var user = User.of(environment.getProperty("seeder.adminusername"), environment.getProperty("seeder.adminpassword"));
            userRepository.save(user);
            user = userRepository.findFirst();

//            var mutableUser = MutableUser.of(user);
//            mutableUser.setEnabled(Boolean.FALSE);
//            mutableUserRepository.save(mutableUser);

            var signatory = Signatory.of(1111111111L, "FirstName", "LastName", "firstname.lastname@sca.test", tenant.getId());
            signatoryRepository.save(signatory);
            signatory = signatoryRepository.findFirst();

            // signatoryRepository.delete(signatory);

            var document = Document.of("demo_document_1", "/documents/", tenant.getId());
            documentRepository.save(document);
            document = documentRepository.findFirst();

            var closing = Closing.of("demo_closing_1", ZoneId.of("Europe/Brussels"), LocalDateTime.now(), tenant.getId());
            closing.addNaturalPersonSignatory(signatory.getId(), document.getId());
            closingRepository.save(closing);
            closing = closingRepository.findFirst();

            closingRepository.setSignatureTimestamp(closing.getId(), signatory.getId(), Instant.now());
            closingRepository.save(closing);

            var legalPerson = LegalPerson.of(1111111111L, tenant.getId());
            legalPerson.addOfficer(signatory.getId(), Function.DIRECTOR, null);
            legalPersonRepository.save(legalPerson);
            legalPerson = legalPersonRepository.findFirst();

            closing.addLegalPersonSignatory(legalPerson.getId());
            closingRepository.save(closing);

            var updatedClosing = closingRepository.findFirst();
        }
    }
}

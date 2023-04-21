package io.github.saxomoose.sca.web.config.initialization;

import io.github.saxomoose.sca.web.enums.Function;
import io.github.saxomoose.sca.web.models.*;
import io.github.saxomoose.sca.web.repositories.db.*;
import io.github.saxomoose.sca.web.services.MinIOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Component
public class Seeder implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(CommandLineRunner.class);

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
    @Autowired
    private MinIOService minioService;

    @Override
    public void run(String... args) {
        load();
    }

    private void load() {
        final var rawFileUUID = UUID.nameUUIDFromBytes("20230308-seed-document".getBytes());
        try {
            minioService.storeFile(
                    "graphion",
                    String.format("/files/%s", rawFileUUID),
                    31599L,
                    new ClassPathResource("minio/20230308-seed-document.pdf").getInputStream()
            );
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        var tenant = Tenant.of("graphion");
        tenantRepository.save(tenant);
        tenant = tenantRepository.select();

        var user = User.of(
                tenant.id(),
                environment.getProperty("seeder.adminusername"),
                environment.getProperty("seeder.adminpassword"));
        userRepository.save(user);
        user = userRepository.select();

//            var mutableUser = MutableUser.of(user);
//            mutableUser.setEnabled(Boolean.FALSE);
//            mutableUserRepository.save(mutableUser);

        var signatory = Signatory.of(tenant.id(), 1111111111L, "FirstName", "LastName", "firstname.lastname@graphion.test");
        signatoryRepository.save(signatory);
        signatory = signatoryRepository.select();

        // signatoryRepository.delete(signatory);

        var document = Document.of(tenant.id(), "20230308-seed-document", String.format("/files/%s", rawFileUUID));
        documentRepository.save(document);
        document = documentRepository.select();

        var closing = Closing.of(tenant.id(), "seed_closing_1", ZoneId.of("Europe/Brussels"), LocalDateTime.now());
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

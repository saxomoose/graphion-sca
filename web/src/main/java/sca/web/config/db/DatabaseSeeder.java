package sca.web.config.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import sca.web.models.Tenant;
import sca.web.models.User;
import sca.web.repositories.TenantRepository;
import sca.web.repositories.UserRepository;

@Component
public class DatabaseSeeder implements CommandLineRunner {
    @Autowired
    private Environment environment;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private UserRepository userRepository;
//    @Autowired
//    private SignatoryRepository signatoryRepository;
//    @Autowired
//    private ClosingRepository closingRepository;
//    @Autowired
//    private DocumentRepository documentRepository;

    @Override
    public void run(String... args) throws Exception {
        load();
    }

    private void load() {
        if (tenantRepository.count() == 0) {
            var tenant = Tenant.of("northstone");
            tenantRepository.save(tenant);
            var randomTenant = tenantRepository.findFirst();
            var user = User.of(environment.getProperty("env.seeder.adminusername"), environment.getProperty("env.seeder.adminpassword"));
            userRepository.save(user);
        }
//

//
//            var randomTenant = tenantRepository.findFirst();
//            var user = new User(randomTenant, environment.getProperty("env.seeder.adminusername"), environment.getProperty("env.seeder.adminpassword"), Boolean.TRUE, Role.ROLE_ADMIN);
//            userRepository.save(user);
//
//            var signatory = new Signatory("Douglas", "Engelbart", "signatory@graphion.test", 46123145365L, Boolean.TRUE);
//            signatoryRepository.save(signatory);
//
//            var closing = new Closing(randomTenant, "demo_closing_1", ZoneOffset.of("+02:00"), LocalDate.now());
//            closingRepository.save(closing);
//
//            var document = new Document(randomTenant, "demo_document_1");
//            documentRepository.save(document);
//
//            // Tenant relationships
////            randomTenant.users
//
//        }
    }
}

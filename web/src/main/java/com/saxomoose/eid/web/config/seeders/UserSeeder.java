package com.saxomoose.eid.web.config.seeders;

import com.saxomoose.eid.web.enums.Role;
import com.saxomoose.eid.web.models.User;
import com.saxomoose.eid.web.repositories.TenantRepository;
import com.saxomoose.eid.web.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class UserSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private Environment environment;

    @Override
    public void run(String... args) throws Exception {
        load();
    }

    private void load() {
        if (userRepository.count() == 0) {
            var Tenant = tenantRepository.findRandom();
            var user = new User(Tenant, environment.getProperty("env.seeder.adminusername"), environment.getProperty("env.seeder.adminpassword"), Boolean.TRUE, Role.ROLE_ADMIN);
            userRepository.save(user);
        }
    }
}

package com.saxomoose.eid.web.config.seeders;

import com.saxomoose.eid.web.models.Tenant;
import com.saxomoose.eid.web.repositories.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class TenantSeeder implements CommandLineRunner {

    @Autowired
    private TenantRepository tenantRepository;

    @Override
    public void run(String... args) throws Exception {
        load();
    }

    private void load() {
        if (tenantRepository.count() == 0) {
            var tenant = new Tenant("northstone");
            tenantRepository.save(tenant);
        }
    }
}

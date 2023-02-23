package com.saxomoose.eid.web.config.seeders;

import com.saxomoose.eid.web.models.Closing;
import com.saxomoose.eid.web.repositories.ClosingRepository;
import com.saxomoose.eid.web.repositories.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Component
@Order(3)
public class ClosingSeeder implements CommandLineRunner {

    @Autowired
    ClosingRepository closingRepository;

    @Autowired
    TenantRepository tenantRepository;

    @Override
    public void run(String... args) throws Exception {
        load();
    }

    private void load() {
        if (closingRepository.count() == 0) {
            var tenant = tenantRepository.findRandom();
            var closing = new Closing(tenant, "demo_closing_1", ZoneOffset.of("+02:00"), LocalDate.now());
            closingRepository.save(closing);
        }
    }
}

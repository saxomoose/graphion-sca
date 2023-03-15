package io.github.saxomoose.sca.web.services;

import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.repositories.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TenantService {
    private final TenantRepository tenantRepository;

    @Autowired
    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public Tenant getTenant(Long tenantId) {
        return tenantRepository.findById(tenantId).orElseThrow();
    }
    public Tenant getTenantByUser(Long userId) { return tenantRepository.findByUserId(userId); }
    public Tenant getTenantByUsername(String username) { return tenantRepository.findByUsername(username); }
}

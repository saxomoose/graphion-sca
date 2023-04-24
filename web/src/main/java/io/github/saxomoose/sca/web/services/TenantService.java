package io.github.saxomoose.sca.web.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.repositories.db.TenantRepository;

@Service
public class TenantService {
    private final TenantRepository tenantRepository;

    @Autowired
    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public Tenant readByTenantId(Long tenantId) {
        return tenantRepository.findById(tenantId).orElseThrow();
    }

    public Tenant readByUserUserId(Long userId) {
        return tenantRepository.select(userId);
    }

    public Tenant read(String username) {
        return tenantRepository.select(username);
    }
}

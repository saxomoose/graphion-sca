package io.github.saxomoose.sca.web.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.saxomoose.sca.web.models.Tenant;
import io.github.saxomoose.sca.web.repositories.db.TenantRepository;

@Service
public class TenantService {
    private final TenantRepository tenantRepository;

    @Autowired
    public TenantService(TenantRepository tenantRepository) 
    {
        this.tenantRepository = tenantRepository;
    }

    public Tenant selectByTenantId(Long id) 
    {
        return tenantRepository.findById(id).orElseThrow();
    }

    public Tenant selectByUserId(Long id) 
    {
        return tenantRepository.select(id);
    }

    public Tenant select(String username) 
    {
        return tenantRepository.select(username);
    }
}

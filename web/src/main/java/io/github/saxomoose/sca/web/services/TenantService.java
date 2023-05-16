package io.github.saxomoose.sca.web.services;

import java.util.Collection;
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

    public Tenant selectById(Long id) 
    {
        return tenantRepository.selectById(id);
    }

    public Collection<Tenant> selectByUserId(Long userId)
    {
        return tenantRepository.selectByUserId(userId);
    }

    // public Tenant select(String username) 
    // {
    //     return tenantRepository.select(username);
    // }
}

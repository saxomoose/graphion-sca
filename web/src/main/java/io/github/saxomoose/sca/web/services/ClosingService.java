package io.github.saxomoose.sca.web.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import io.github.saxomoose.sca.web.models.Closing;
import io.github.saxomoose.sca.web.services.repositories.ClosingRepository;

@Service
public class ClosingService {

    private final ClosingRepository closingRepository;

    @Autowired
    public ClosingService(ClosingRepository closingRepository)
    {
        this.closingRepository = closingRepository;
    }
    
    public Page<Closing> selectPage(final Pageable pageable, final Long tenantId)
    {
        return closingRepository.findAllByTenant(pageable, tenantId);
    }
}

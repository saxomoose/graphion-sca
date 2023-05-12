package io.github.saxomoose.sca.web.services;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import io.github.saxomoose.sca.web.models.Signatory;
import io.github.saxomoose.sca.web.repositories.db.SignatoryRepository;

@Service
public class SignatoryService {
    private final SignatoryRepository signatoryRepository;

    @Autowired
    public SignatoryService(SignatoryRepository signatoryRepository)
    {
        this.signatoryRepository = signatoryRepository;
    }

    public Page<Signatory> selectPage(final Pageable pageable, final Long tenantId)
    {
        return signatoryRepository.findAllByTenant(pageable, tenantId);
    }

    public Signatory select(UUID uuid)
    {
        return signatoryRepository.select(uuid);
    }

}

package io.github.saxomoose.sca.web.services;

import org.springframework.stereotype.Service;
import io.github.saxomoose.sca.web.models.Signatory;
import io.github.saxomoose.sca.web.repositories.db.SignatoryRepository;

@Service
public class SignatoryService {
    private final SignatoryRepository signatoryRepository;

    public SignatoryService(SignatoryRepository signatoryRepository)
    {
        this.signatoryRepository = signatoryRepository;
    }

    public Signatory select(Long nationalRegistrationNumber)
    {
        return signatoryRepository.select(nationalRegistrationNumber);
    }

}

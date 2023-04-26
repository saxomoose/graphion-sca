package io.github.saxomoose.sca.web.config;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService {
    private String activeProfile;
    private String localOrigin;

    public ConfigurationService(Environment environment) {
        this.activeProfile = environment.getProperty("spring.profiles.active");
        this.localOrigin = environment.getProperty("web-eid-auth-token.validation.local-origin");
    }

    public String activeProfile() {
        return activeProfile;
    }

    public String localOrigin() {
        return localOrigin;
    }
}

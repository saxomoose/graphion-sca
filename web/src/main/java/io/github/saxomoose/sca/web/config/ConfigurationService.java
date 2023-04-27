package io.github.saxomoose.sca.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService {
    private final String activeProfile;
    private final String localOrigin;
    private final String objectStorageAccesskey;
    private final String objectStorageEndpoint;
    private final String objectStorageSecretkey;

    @Autowired
    public ConfigurationService(Environment environment) {
        this.activeProfile = environment.getProperty("spring.profiles.active");
        this.localOrigin = environment.getProperty("web-eid-auth-token.validation.local-origin");
        this.objectStorageAccesskey = environment.getProperty("objectstorage.accesskey");
        this.objectStorageEndpoint = environment.getProperty("objectstorage.endpoint");
        this.objectStorageSecretkey = environment.getProperty("objectstorage.secretkey");
    }

    public String activeProfile() {
        return activeProfile;
    }

    public String localOrigin() {
        return localOrigin;
    }

    public String objectStorageAccesskey() {
        return objectStorageAccesskey;
    }

    public String objectStorageEndpoint() {
        return objectStorageEndpoint;
    }

    public String objectStorageSecretkey() {
        return objectStorageSecretkey;
    }
}

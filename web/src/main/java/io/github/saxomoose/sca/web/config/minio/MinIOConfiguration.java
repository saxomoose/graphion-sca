package io.github.saxomoose.sca.web.config.minio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import io.github.saxomoose.sca.web.config.ConfigurationService;
import io.minio.MinioClient;

@Configuration
public class MinIOConfiguration {
    private final ConfigurationService configurationService;

    @Autowired
    public MinIOConfiguration(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Bean
    @Primary
    public MinioClient minioClient() {
        return new MinioClient.Builder()
                .credentials(configurationService.objectStorageAccesskey(),
                        configurationService.objectStorageSecretkey())
                .endpoint(configurationService.objectStorageEndpoint()).build();
    }

}

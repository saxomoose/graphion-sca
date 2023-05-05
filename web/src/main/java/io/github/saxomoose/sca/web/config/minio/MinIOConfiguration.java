package io.github.saxomoose.sca.web.config.minio;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.github.saxomoose.sca.web.config.ConfigurationService;
import io.minio.MinioClient;

@Configuration
public class MinIOConfiguration {
    @Bean
    @Primary
    public MinioClient minioClient(final ConfigurationService configurationService) {
        return new MinioClient.Builder()
                .credentials(configurationService.objectStorageAccesskey(),
                        configurationService.objectStorageSecretkey())
                .endpoint(configurationService.objectStorageEndpoint()).build();
    }

}

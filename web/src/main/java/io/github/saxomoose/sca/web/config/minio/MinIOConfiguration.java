package io.github.saxomoose.sca.web.config.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MinIOConfiguration {
    @Value("${objectstorage.endpoint}")
    private String endpoint;
    @Value("${objectstorage.accesskey}")
    private String accessKey;
    @Value("${objectstorage.secretkey}")
    private String secretKey;

    @Bean
    @Primary
    public MinioClient minioClient() {
        return new MinioClient.Builder()
                .credentials(accessKey, secretKey)
                .endpoint(endpoint)
                .build();
    }


}

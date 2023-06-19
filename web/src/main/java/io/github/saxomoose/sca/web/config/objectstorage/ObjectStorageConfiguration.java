package io.github.saxomoose.sca.web.config.objectstorage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import io.minio.MinioClient;

@Configuration
public class ObjectStorageConfiguration {
    @Bean
    public MinioClient minioClient(final Environment environment) 
    {
        return new MinioClient.Builder()
            .credentials(
                environment.getProperty("objectstorage.accesskey"),
                environment.getProperty("objectstorage.secretkey"))
            .endpoint(environment.getProperty("objectstorage.endpoint"))
            .build();
    }
}

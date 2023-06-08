package io.github.saxomoose.sca.web.config.minio;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import io.minio.MinioClient;

@Configuration
public class MinIOConfiguration {
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

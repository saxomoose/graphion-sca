package io.github.saxomoose.sca.web.config.security;

import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import eu.webeid.security.challenge.ChallengeNonceGenerator;
import eu.webeid.security.challenge.ChallengeNonceGeneratorBuilder;
import eu.webeid.security.challenge.ChallengeNonceStore;
import eu.webeid.security.validator.AuthTokenValidator;
import eu.webeid.security.validator.AuthTokenValidatorBuilder;
import io.github.saxomoose.sca.web.enums.Path;
import io.github.saxomoose.sca.web.services.MinIOService;
import io.minio.Result;
import io.minio.messages.Item;

@Configuration
public class PKIConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(PKIConfiguration.class);

    private final Environment environment;
    private final MinIOService minioService;

    public PKIConfiguration(Environment environment, MinIOService minioService) {
        this.environment = environment;
        this.minioService = minioService;
    }

    /**
     * Challenge nonce generator.
     */
    @Bean
    public ChallengeNonceGenerator generator(final ChallengeNonceStore challengeNonceStore) 
    {
        return new ChallengeNonceGeneratorBuilder()
                .withNonceTtl(Duration.ofMinutes(5))
                .withChallengeNonceStore(challengeNonceStore).build();
    }

    /**
     * TODO: evaluate whether loading should be refactored in configuration service.
     * Specify which **intermediate** certificate authorities (CAs) are trusted to issue the eID
     * authentication and OCSP responder certificates.
     */
    public X509Certificate[] belgianCACertificates() 
    {
        List<X509Certificate> caCertificates = new ArrayList<>();
        final Iterable<Result<Item>> certListing = minioService.listObjects(
                environment.getProperty("objectstorage.default-bucket"),
                Path.BELGIUM_CERTS.toString());
        try {
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            for (Result<Item> result : certListing) {
                X509Certificate caCertificate = 
                    (X509Certificate) certFactory.generateCertificate(
                        minioService.objectAsStream(
                            environment.getProperty("objectstorage.default-bucket"), 
                            result.get().objectName()));
                try {
                    caCertificate.checkValidity();
                    caCertificates.add(caCertificate);
                } catch (CertificateException exception) {
                    logger.error(exception.getMessage());
                }
            }
            return caCertificates.toArray(new X509Certificate[0]);
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    /**
     * Authentication token validator.
     */
    @Bean
    public AuthTokenValidator validator() 
    {
        try {
            AuthTokenValidatorBuilder validatorBuilder = new AuthTokenValidatorBuilder()
                .withSiteOrigin(URI.create(environment.getProperty("web-eid-auth-token.validation.local-origin")))
                .withTrustedCertificateAuthorities(belgianCACertificates());
            if (environment.getProperty("spring.profiles.active").equals("dev")) {
                validatorBuilder = validatorBuilder.withoutUserCertificateRevocationCheckWithOcsp();
            }
            return validatorBuilder.build();
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }
}

package io.github.saxomoose.sca.web.config.security.pki;

import java.net.URI;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import eu.webeid.security.challenge.ChallengeNonceGenerator;
import eu.webeid.security.challenge.ChallengeNonceGeneratorBuilder;
import eu.webeid.security.challenge.ChallengeNonceStore;
import eu.webeid.security.exceptions.JceException;
import eu.webeid.security.validator.AuthTokenValidator;
import eu.webeid.security.validator.AuthTokenValidatorBuilder;
import io.github.saxomoose.sca.web.config.ConfigurationService;

@Configuration
public class ValidationConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ValidationConfiguration.class);

    private static final long CHALLENGE_NONCE_TTL_MINUTES = 5;
    private static final String CERTS_RESOURCE_PATH = "/certs/";
    private static final String TRUSTED_CERTIFICATES_JKS = "trusted_certificates.jks";

    private final ConfigurationService configurationService;
    private final ChallengeNonceStore challengeNonceStore;

    public ValidationConfiguration(ConfigurationService configurationService,
            ChallengeNonceStore challengeNonceStore) {
        this.configurationService = configurationService;
        this.challengeNonceStore = challengeNonceStore;
    }

    @Bean
    public ChallengeNonceGenerator generator() {
        return new ChallengeNonceGeneratorBuilder()
                .withNonceTtl(Duration.ofMinutes(CHALLENGE_NONCE_TTL_MINUTES))
                .withChallengeNonceStore(challengeNonceStore).build();
    }

    @Bean
    public X509Certificate[] loadTrustedCACertificatesFromCerFiles() {
        List<X509Certificate> caCertificates = new ArrayList<>();
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            PathMatchingResourcePatternResolver resolver =
                    new PathMatchingResourcePatternResolver();
            Resource[] resources =
                    // resolver.getResources(CERTS_RESOURCE_PATH + activeProfile + "/*.cer");
                    resolver.getResources("/certs/dev/*.cer");

            for (Resource resource : resources) {
                X509Certificate caCertificate = (X509Certificate) certFactory
                        .generateCertificate(resource.getInputStream());
                caCertificates.add(caCertificate);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize trusted CA certificates.", e);
        }
        return caCertificates.toArray(new X509Certificate[0]);
    }

    // @Bean
    // public X509Certificate[] loadTrustedCACertificatesFromTrustStore() {
    // List<X509Certificate> caCertificates = new ArrayList<>();
    // try (InputStream is = ValidationConfiguration.class.getResourceAsStream(
    // CERTS_RESOURCE_PATH + activeProfile + "/" + TRUSTED_CERTIFICATES_JKS)) {
    // if (is == null) {
    // logger.info("Truststore file {} not found for {} profile", TRUSTED_CERTIFICATES_JKS,
    // activeProfile);
    // return new X509Certificate[0];
    // }
    // KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
    // keystore.load(is, yamlConfig().getTrustStorePassword().toCharArray());
    // Enumeration<String> aliases = keystore.aliases();
    // while (aliases.hasMoreElements()) {
    // String alias = aliases.nextElement();
    // X509Certificate certificate = (X509Certificate) keystore.getCertificate(alias);
    // caCertificates.add(certificate);
    // }
    // } catch (IOException | CertificateException | KeyStoreException
    // | NoSuchAlgorithmException e) {
    // throw new RuntimeException(
    // "Error initializing trusted CA certificates from trust store.", e);
    // }

    // return caCertificates.toArray(new X509Certificate[0]);
    // }

    @Bean
    public AuthTokenValidator validator() {
        try {
            AuthTokenValidatorBuilder validatorBuilder = new AuthTokenValidatorBuilder()
                    .withSiteOrigin(URI.create(configurationService.localOrigin()))
                    .withTrustedCertificateAuthorities(loadTrustedCACertificatesFromCerFiles());
            // .withTrustedCertificateAuthorities(loadTrustedCACertificatesFromTrustStore());
            // if (activeProfile.equals("dev")) {
            // Enable support for ESTEID 2015 test certificates in development profile.
            validatorBuilder = validatorBuilder
                    .withNonceDisabledOcspUrls(URI.create("http://aia.demo.sk.ee/esteid2015"));
            // }
            return validatorBuilder.build();
        } catch (JceException e) {
            throw new RuntimeException("Failed to build the Web eID auth token validator.", e);
        }
    }
}

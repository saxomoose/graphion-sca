package io.github.saxomoose.sca.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;

@Configuration
public class DSSConfiguration {
    @Bean
	public PAdESService padesService() {
		final var service = new PAdESService(certificateVerifier());
		// service.setTspSource(tspSource);
		return service;
	}

    @Bean
	public CertificateVerifier certificateVerifier() {
		final var certificateVerifier = new CommonCertificateVerifier();
		// certificateVerifier.setCrlSource(cachedCRLSource());
		// certificateVerifier.setOcspSource(cachedOCSPSource());
		// certificateVerifier.setAIASource(cachedAIASource());
		// certificateVerifier.setTrustedCertSources(trustedListSource(), trustedCertificateSource());

		// Default configs
		// certificateVerifier.setAlertOnMissingRevocationData(new ExceptionOnStatusAlert());
		// certificateVerifier.setCheckRevocationForUntrustedChains(false);

		return certificateVerifier;
	}
}

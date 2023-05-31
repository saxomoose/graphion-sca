package io.github.saxomoose.sca.web.config;

import java.security.cert.X509Certificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.service.http.commons.TimestampDataLoader;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.service.tsp.OnlineTSPSource;
import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.spi.x509.aia.DefaultAIASource;
import eu.europa.esig.dss.spi.x509.tsp.TSPSource;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;

@Configuration
public class DSSConfiguration {

    private final Environment environment;
    private final X509Certificate[] trustedCACertificates;

    @Autowired
    public DSSConfiguration(Environment environment, X509Certificate[] trustedCACertificates)
    {
        this.environment = environment;
        this.trustedCACertificates = trustedCACertificates;
    }

    @Bean
	public PAdESService padesService() {
		final var service = new PAdESService(certificateVerifier());
		service.setTspSource(tspSource());
		return service;
	}

    @Bean
	public CertificateVerifier certificateVerifier() {
		final var certificateVerifier = new CommonCertificateVerifier();
		certificateVerifier.setTrustedCertSources(trustedCertificateSource());
        certificateVerifier.setAIASource(new DefaultAIASource());
        certificateVerifier.setOcspSource(new OnlineOCSPSource());
        // certificateVerifier.setCrlSource(new OnlineCRLSource());

		// Default configs
		// certificateVerifier.setAlertOnMissingRevocationData(new ExceptionOnStatusAlert());
		certificateVerifier.setCheckRevocationForUntrustedChains(true);

		return certificateVerifier;
	}

	public CommonTrustedCertificateSource trustedCertificateSource() {
		CommonTrustedCertificateSource trustedCertificateSource = new CommonTrustedCertificateSource();
        for (X509Certificate caCertificate : trustedCACertificates) {
            final var certificateToken = new CertificateToken(caCertificate);
            trustedCertificateSource.addCertificate(certificateToken);
        }
		return trustedCertificateSource;
	}

    public TSPSource tspSource()
    {
        OnlineTSPSource tspSource = new OnlineTSPSource(environment.getProperty("timestamp.url"));
        tspSource.setDataLoader(new TimestampDataLoader());
        return tspSource;
    }


}

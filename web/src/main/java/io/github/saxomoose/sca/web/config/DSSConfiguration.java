package io.github.saxomoose.sca.web.config;

import java.security.cert.X509Certificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import eu.europa.esig.dss.alert.ExceptionOnStatusAlert;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.TimestampDataLoader;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.service.tsp.OnlineTSPSource;
import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.spi.x509.aia.AIASource;
import eu.europa.esig.dss.spi.x509.aia.DefaultAIASource;
import eu.europa.esig.dss.spi.x509.revocation.crl.CRLSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPSource;
import eu.europa.esig.dss.spi.x509.tsp.TSPSource;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.OCSPFirstRevocationDataLoadingStrategyFactory;
import io.github.saxomoose.sca.web.config.security.PKIConfiguration;

@Configuration
public class DSSConfiguration {

    private final Environment environment;
    private final PKIConfiguration pkiConfiguration;

    @Autowired
    public DSSConfiguration(Environment environment, PKIConfiguration pkiConfiguration)
    {
        this.environment = environment;
        this.pkiConfiguration = pkiConfiguration;
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
        certificateVerifier.setAIASource(aiaSource());
        certificateVerifier.setCrlSource(crlSource());
        certificateVerifier.setOcspSource(oscpSource());
        certificateVerifier.setDefaultDigestAlgorithm(DigestAlgorithm.SHA256);
        certificateVerifier.setRevocationDataLoadingStrategyFactory(new OCSPFirstRevocationDataLoadingStrategyFactory());

		// Default configs
		certificateVerifier.setAlertOnMissingRevocationData(new ExceptionOnStatusAlert());
		certificateVerifier.setCheckRevocationForUntrustedChains(false);

		return certificateVerifier;
	}

	public CommonTrustedCertificateSource trustedCertificateSource() {
		CommonTrustedCertificateSource trustedCertificateSource = new CommonTrustedCertificateSource();
        for (X509Certificate caCertificate : pkiConfiguration.trustedCACertificates()) {
            final var certificateToken = new CertificateToken(caCertificate);
            trustedCertificateSource.addCertificate(certificateToken);
        }
		return trustedCertificateSource;
	}

    @Bean
    public AIASource aiaSource()
    {
        return new DefaultAIASource();
    }

    public CRLSource crlSource()
    {
        return new OnlineCRLSource();
    }

    public OCSPSource oscpSource()
    {
        final var ocspSource = new OnlineOCSPSource();
        ocspSource.setCertIDDigestAlgorithm(DigestAlgorithm.SHA256);
        return ocspSource;
    }

    public TSPSource tspSource()
    {
        final var tspSource = new OnlineTSPSource(environment.getProperty("timestamp.url"));
        tspSource.setDataLoader(new TimestampDataLoader());
        return tspSource;
    }


}

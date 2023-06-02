package io.github.saxomoose.sca.web.config;

import java.io.File;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import eu.europa.esig.dss.alert.ExceptionOnStatusAlert;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.service.http.commons.TimestampDataLoader;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.service.tsp.OnlineTSPSource;
import eu.europa.esig.dss.spi.client.http.DSSFileLoader;
import eu.europa.esig.dss.spi.client.http.IgnoreDataLoader;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.spi.x509.aia.AIASource;
import eu.europa.esig.dss.spi.x509.aia.DefaultAIASource;
import eu.europa.esig.dss.spi.x509.revocation.crl.CRLSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPSource;
import eu.europa.esig.dss.spi.x509.tsp.TSPSource;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.LOTLSource;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.OCSPFirstRevocationDataLoadingStrategyFactory;
import io.github.saxomoose.sca.web.config.security.PKIConfiguration;
import io.github.saxomoose.sca.web.enums.Path;
import io.github.saxomoose.sca.web.services.MinIOService;
import io.minio.Result;
import io.minio.messages.Item;

@Configuration
public class DSSConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(DSSConfiguration.class);

    private final Environment environment;
    private final MinIOService minioService;
    private final PKIConfiguration pkiConfiguration;

    @Autowired
    public DSSConfiguration(
        Environment environment, 
        MinIOService minioService, 
        PKIConfiguration pkiConfiguration)
    {
        this.environment = environment;
        this.minioService = minioService;
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
        final TLValidationJob job = lotlJob();
        final var ojCertificates = new TrustedListsCertificateSource();
        job.setTrustedListCertificateSource(ojCertificates);
        job.offlineRefresh();
		final var certificateVerifier = new CommonCertificateVerifier();
		certificateVerifier.setTrustedCertSources(ojCertificates, belgianCACertificates());
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

    @Bean
	public CommonTrustedCertificateSource belgianCACertificates() {
		final var certificateSource = new CommonTrustedCertificateSource();
        for (X509Certificate caCertificate : pkiConfiguration.belgianCACertificates()) {
            final var certificateToken = new CertificateToken(caCertificate);
            certificateSource.addCertificate(certificateToken);
        }
		return certificateSource;
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
        final var tspSource = new OnlineTSPSource(environment.getProperty("dss.timestamp.url"));
        tspSource.setDataLoader(new TimestampDataLoader());
        return tspSource;
    }

	public TLValidationJob lotlJob() {
		TLValidationJob job = new TLValidationJob();
		job.setTrustedListCertificateSource(trustedListSource());
		job.setListOfTrustedListSources(listOfTrustedListSources());
		job.setOfflineDataLoader(offlineLoader());
		job.setOnlineDataLoader(onlineLoader());
		return job;
	}

    public TrustedListsCertificateSource trustedListSource() {
		return new TrustedListsCertificateSource();
	}

    public LOTLSource[] listOfTrustedListSources() {
		final var lotlSourceList = new ArrayList<LOTLSource>();
		lotlSourceList.add(europeanLOTL());
		return lotlSourceList.toArray(new LOTLSource[0]);
	}

    public LOTLSource europeanLOTL() {
		final var lotlSource = new LOTLSource();
		lotlSource.setUrl(environment.getProperty("dss.lotl.url"));
		lotlSource.setCertificateSource(ojCertificates());
		return lotlSource;
	}

    public CommonTrustedCertificateSource ojCertificates() 
    {
        final var certificateSource = new CommonTrustedCertificateSource();
        final Iterable<Result<Item>> certListing = minioService.listObjects(
                environment.getProperty("objectstorage.default-bucket"),
                Path.OJ_CERTS.toString());
        try {
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            for (Result<Item> result : certListing) {
                X509Certificate ojCertificate = 
                    (X509Certificate) certFactory.generateCertificate(
                        minioService.objectAsStream(
                            environment.getProperty("objectstorage.default-bucket"), 
                            result.get().objectName()));
                    certificateSource.addCertificate(new CertificateToken(ojCertificate));
            }
            return certificateSource; 
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

	public DSSFileLoader offlineLoader() {
		FileCacheDataLoader offlineFileLoader = new FileCacheDataLoader();
		offlineFileLoader.setCacheExpirationTime(-1);
		offlineFileLoader.setDataLoader(new IgnoreDataLoader());
		offlineFileLoader.setFileCacheDirectory(tlCacheDirectory());
		return offlineFileLoader;
	}

    public DSSFileLoader onlineLoader() {
		FileCacheDataLoader onlineFileLoader = new FileCacheDataLoader();
		onlineFileLoader.setCacheExpirationTime(0);
		onlineFileLoader.setDataLoader(new CommonsDataLoader());
		onlineFileLoader.setFileCacheDirectory(tlCacheDirectory());
		return onlineFileLoader;
	}

	public File tlCacheDirectory() {
        final var path = String.format(
            "%s/%s",
            System.getProperty("user.dir"),
            Path.DSS_TLCACHE);
		final var tslCache = new File(path);
        tslCache.mkdirs();
		return tslCache;
	}

    
}

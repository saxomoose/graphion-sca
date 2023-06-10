package io.github.saxomoose.sca.web.config;

import java.io.File;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;
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
import eu.europa.esig.dss.pdf.IPdfObjFactory;
import eu.europa.esig.dss.pdf.ServiceLoaderPdfObjFactory;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.service.http.commons.OCSPDataLoader;
import eu.europa.esig.dss.service.http.commons.TimestampDataLoader;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.service.tsp.OnlineTSPSource;
import eu.europa.esig.dss.signature.resources.TempFileResourcesHandlerBuilder;
import eu.europa.esig.dss.spi.client.http.DSSFileLoader;
import eu.europa.esig.dss.spi.client.http.IgnoreDataLoader;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.spi.x509.aia.AIASource;
import eu.europa.esig.dss.spi.x509.aia.DefaultAIASource;
import eu.europa.esig.dss.spi.x509.revocation.crl.CRLSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPSource;
import eu.europa.esig.dss.spi.x509.tsp.CompositeTSPSource;
import eu.europa.esig.dss.spi.x509.tsp.TSPSource;
import eu.europa.esig.dss.tsl.function.TLPredicateFactory;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.LOTLSource;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.OCSPFirstRevocationDataLoadingStrategyFactory;
import eu.europa.esig.trustedlist.jaxb.tsl.TSPType;
import io.github.saxomoose.sca.web.enums.PathEnum;
import io.github.saxomoose.sca.web.services.ObjectStorageService;
import io.minio.Result;
import io.minio.messages.Item;

@Configuration
public class DSSConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(DSSConfiguration.class);

    private final Environment environment;
    private final File tlCacheDirectory;
    private final ObjectStorageService objectStorageService;

    @Autowired
    public DSSConfiguration(
        Environment environment, 
        ObjectStorageService objectStorageService)
    {
        this.environment = environment;
        this.tlCacheDirectory = tlCacheDirectory();
        this.objectStorageService = objectStorageService;
    }

    @Bean
	public PAdESService padesService() 
    {
		final PAdESService service = new PAdESService(certificateVerifier());
		service.setTspSource(tspSource());
        IPdfObjFactory pdfObjFactory = new ServiceLoaderPdfObjFactory();
        pdfObjFactory.setResourcesHandlerBuilder(tempFileResourcesHandlerBuilder());
        service.setPdfObjFactory(pdfObjFactory);
		return service;
	}

    @Bean
    public TempFileResourcesHandlerBuilder tempFileResourcesHandlerBuilder()
    {
        final TempFileResourcesHandlerBuilder tempFileResourcesHandlerBuilder = new TempFileResourcesHandlerBuilder();
        tempFileResourcesHandlerBuilder.setTempFileDirectory(tmpWorkbenchDirectory());
        return tempFileResourcesHandlerBuilder;
    }

    @Bean
	public CertificateVerifier certificateVerifier() 
    {
		final CertificateVerifier certificateVerifier = new CommonCertificateVerifier();
		certificateVerifier.setTrustedCertSources(caCertificates(), timestampCertificates());
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

    private CertificateSource caCertificates() 
    {
		final TLValidationJob job = new TLValidationJob();
        final LOTLSource lotlSource = new LOTLSource();
        final TrustedListsCertificateSource caCertificates = new TrustedListsCertificateSource();
        lotlSource.setUrl(environment.getProperty("dss.lotl.eu"));
		lotlSource.setCertificateSource(ojSignatureCertificates());
        lotlSource.setPivotSupport(false);
        lotlSource.setTlPredicate(TLPredicateFactory.createEUTLCountryCodePredicate("BE"));
        lotlSource.setTrustServiceProviderPredicate((TSPType tspType) -> {
            final String tradeName = tspType.getTSPInformation().getTSPTradeName().getName().get(0).getValue();
            return tradeName.equals("VATBE-0475396406") 
                || tradeName.equals("NTRBE-0671516647") 
                || tradeName.equals("VATBE-0537698318");
        });
		job.setListOfTrustedListSources(lotlSource);
		job.setOfflineDataLoader(offlineLoader());
		job.setOnlineDataLoader(onlineLoader());
		job.setTrustedListCertificateSource(caCertificates);
        job.onlineRefresh();
		return caCertificates;
	}

    private CertificateSource ojSignatureCertificates() 
    {
        return signatureCertificates(PathEnum.CERTS_DSS_EU);
    }

	private DSSFileLoader offlineLoader() 
    {
		final FileCacheDataLoader offlineFileLoader = new FileCacheDataLoader();
		offlineFileLoader.setCacheExpirationTime(-1);
		offlineFileLoader.setDataLoader(new IgnoreDataLoader());
		offlineFileLoader.setFileCacheDirectory(tlCacheDirectory);
		return offlineFileLoader;
	}

    private DSSFileLoader onlineLoader() 
    {
		final FileCacheDataLoader onlineFileLoader = new FileCacheDataLoader();
		onlineFileLoader.setCacheExpirationTime(0);
		onlineFileLoader.setDataLoader(new CommonsDataLoader());
		onlineFileLoader.setFileCacheDirectory(tlCacheDirectory);
		return onlineFileLoader;
	}

    private File tlCacheDirectory()
    {
        final String path = String.format(
            "%s/%s",
            System.getProperty("user.dir"),
            PathEnum.DSS_TRUST_LISTS);
        final File tlCacheDirectory = new File(path); 
        tlCacheDirectory.mkdirs();
        return tlCacheDirectory;
    }

    private AIASource aiaSource()
    {
        return new DefaultAIASource(new CommonsDataLoader());
    }

    private CRLSource crlSource()
    {
        final OnlineCRLSource crlSource = new OnlineCRLSource();
        crlSource.setDataLoader(new CommonsDataLoader());
        return crlSource;
    }

    private OCSPSource oscpSource()
    {
        final OnlineOCSPSource ocspSource = new OnlineOCSPSource();
        ocspSource.setCertIDDigestAlgorithm(DigestAlgorithm.SHA256);
        ocspSource.setDataLoader(new OCSPDataLoader());
        return ocspSource;
    }

    private TSPSource tspSource()
    {
        final TimestampDataLoader timestampDataLoader = new TimestampDataLoader();
        final OnlineTSPSource beTSPSource = new OnlineTSPSource(environment.getProperty("dss.timestamp.be"));
        beTSPSource.setDataLoader(timestampDataLoader);
        final CompositeTSPSource compositeTSPSource = new CompositeTSPSource();
        compositeTSPSource.setTspSources(Map.of(
            "be", beTSPSource));
        return compositeTSPSource;
    }

    private CertificateSource timestampCertificates()
    {
        return signatureCertificates(PathEnum.CERTS_DSS_TIMESTAMP);
    }

    private CertificateSource signatureCertificates(final PathEnum path)
    {
        final CertificateSource certificateSource = new CommonTrustedCertificateSource();
        final Iterable<Result<Item>> certListing = objectStorageService.listObjects(
                environment.getProperty("objectstorage.admin-bucket"),
                path.toString());
        try {
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            for (final Result<Item> result : certListing) {
                X509Certificate signatureCertificate = 
                    (X509Certificate) certFactory.generateCertificate(
                        objectStorageService.objectAsStream(
                            environment.getProperty("objectstorage.admin-bucket"), 
                            result.get().objectName()));
                certificateSource.addCertificate(new CertificateToken(signatureCertificate));
            }
            return certificateSource; 
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    private File tmpWorkbenchDirectory()
    {
        final String path = String.format(
            "%s/%s",
            System.getProperty("user.dir"),
            PathEnum.DSS_WORKBENCH_TMP);
        final File tmpWorkbenchDirectory = new File(path); 
        tmpWorkbenchDirectory.mkdirs();
        return tmpWorkbenchDirectory;
    }
}

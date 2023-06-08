package io.github.saxomoose.sca.web.utils;

import java.io.File;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import io.github.saxomoose.sca.web.controllers.pki.DigestPayload;
import io.github.saxomoose.sca.web.models.Tenant;

public class Tuple<X, Y> {
    final X first;
    final Y second;

    public static Tuple<Tenant, List<GrantedAuthority>> of(Tenant tenant, List<GrantedAuthority> authorities)
    {
        return new Tuple<Tenant, List<GrantedAuthority>>(tenant, authorities);
    }

    public static Tuple<DigestPayload, PAdESSignatureParameters> of(DigestPayload digest, PAdESSignatureParameters parameters)
    {
        return new Tuple<DigestPayload, PAdESSignatureParameters>(digest, parameters);
    }

    public static Tuple<DSSDocument, File> of(DSSDocument signedDocument, File file) {
        return new Tuple<DSSDocument, File>(signedDocument, file);
    }

    public Tuple(X first, Y second)
    {
        this.first = first;
        this.second = second;
    }

    public X first()
    {
        return first;
    }

    public Y second()
    {
        return second;
    }



}

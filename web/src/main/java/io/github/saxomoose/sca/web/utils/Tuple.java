package io.github.saxomoose.sca.web.utils;

import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import io.github.saxomoose.sca.web.controllers.pki.DigestPayload;
import io.github.saxomoose.sca.web.enums.Role;
import io.github.saxomoose.sca.web.models.Tenant;

public class Tuple<X, Y> {
    final X first;
    final Y second;

    public static Tuple<DigestPayload, PAdESSignatureParameters> of(DigestPayload digest, PAdESSignatureParameters parameters)
    {
        return new Tuple<DigestPayload, PAdESSignatureParameters>(digest, parameters);
    }

    public static Tuple<Tenant, Role> of(Tenant tenant, Role role)
    {
        return new Tuple<Tenant, Role>(tenant, role);
    }

    public static Tuple<Tenant, List<GrantedAuthority>> of(Tenant tenant, List<GrantedAuthority> role)
    {
        return new Tuple<Tenant, List<GrantedAuthority>>(tenant, role);
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

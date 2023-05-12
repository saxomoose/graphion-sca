package io.github.saxomoose.sca.web.models;

import java.util.List;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;

public interface Authenticable {
    List<GrantedAuthority> role();
    UUID uuid();
}

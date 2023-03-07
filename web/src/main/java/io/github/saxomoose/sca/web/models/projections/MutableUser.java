package io.github.saxomoose.sca.web.models.projections;

import io.github.saxomoose.sca.web.enums.Role;
import io.github.saxomoose.sca.web.models.User;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(schema = "sca", name = "user")
public class MutableUser  {
    @Id
    private final Long id;
    private final String username;
    private String password;
    private Boolean enabled;
    private Role role;

    private Long tenant;

    @Version
    private Long version;
    @CreatedBy
    private Long createdBy;
    @LastModifiedBy
    private Long lastModifiedBy;
    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public static MutableUser of(User user) {
        return new MutableUser(user.getId(), user.getUsername(), user.getPassword(), user.isEnabled(), user.getRole(), user.getTenant(), user.getVersion(), user.getCreatedBy(), user.getLastModifiedBy(), user.getCreatedDate(), user.getLastModifiedDate());
    }

    public MutableUser(Long id, String username, String password, Boolean enabled, Role role, Long tenant, Long version, Long createdBy, Long lastModifiedBy, LocalDateTime createdDate, LocalDateTime lastModifiedDate) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.role = role;
        this.tenant = tenant;
        this.version = version;
        this.createdBy = createdBy;
        this.lastModifiedBy = lastModifiedBy;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}

package sca.web.models;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class Closing {
    @Id
    private Long id;

    @NotNull
    @CreatedBy
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

//    @ManyToMany(targetEntity = Signatory.class)
//    @JoinTable(name = "closing_signatory",
//            joinColumns = {@JoinColumn(name = "closing_id")},
//            inverseJoinColumns = {@JoinColumn(name = "signatory_id")})
//    private Collection<Signatory> signatories;

    @NotNull
    private String name;

    @NotNull
    private ZoneOffset zoneOffset;

    @NotNull
    private LocalDate date;

    @CreatedDate
    private Instant createdTimestamp;

    @LastModifiedDate
    protected Instant lastModifiedTimestamp;

    public Closing() { }

    public Closing(Tenant tenant, String name, ZoneOffset zoneOffset, LocalDate date) {
        this.tenant = tenant;
        this.name = name;
        this.zoneOffset = zoneOffset;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public ZoneOffset getZoneOffset() { return zoneOffset; }

    public void setZoneOffset(ZoneOffset zoneOffset) { this.zoneOffset = zoneOffset; }

    public LocalDate getDate() { return date; }

    public void setDate(LocalDate date) { this.date = date; }
}

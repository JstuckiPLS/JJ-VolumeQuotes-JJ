package com.pls.ltlrating.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.shared.Status;

/**
 * Ltl Zone Entity.
 *
 * @author Artem Arapov
 *
 */
@Entity
@Table(schema = "FLATBED", name = "LTL_ZONES")
public class LtlZonesEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 5970474188060594288L;

    public static final String UPDATE_STATUS_STATEMENT = "com.pls.ltlrating.domain.LtlZonesEntity.UPDATE_STATUS_STATEMENT";

    public static final String INACTIVATE_BY_PROFILE_ID = "com.pls.ltlrating.domain.LtlZonesEntity.INACTIVATE_BY_PROFILE_ID";

    public static final String FIND_CSP_ENTITY_BY_COPIED_FROM = "com.pls.ltlrating.domain.LtlZonesEntity.FIND_CSP_ENTITY_BY_COPIED_FROM";

    public static final String GET_ZONE_WITH_MATCHING_NAME = "com.pls.ltlrating.domain.LtlZonesEntity.GET_ZONE_WITH_MATCHING_NAME";

    public static final String GET_ZONE_BY_PROFILE_DETAIL_ID_AND_NAME =
            "com.pls.ltlrating.domain.LtlZonesEntity.GET_ZONE_BY_PROFILE_DETAIL_ID_AND_NAME";

    public static final String UPDATE_CSP_STATUS_STATEMENT =
            "com.pls.ltlrating.domain.LtlZonesEntity.UPDATE_CSP_STATUS_BY_COPIED_FROM_STATEMENT";

    public static final String INACTIVATE_CSP_BY_DETAIL_ID =
            "com.pls.ltlrating.domain.LtlZonesEntity.INACTIVATE_CSP_BY_DETAIL_ID";

    public static final String GET_ZONE_BY_STATUS_AND_PROFILE_ID = "com.pls.ltlrating.domain.LtlZonesEntity.GET_ZONE_BY_STATUS_AND_PROFILE_ID";
    public static final String GET_MISSING_ZONES = "com.pls.ltlrating.domain.LtlZonesEntity.GET_MISSING_ZONES";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LTL_ZONES_SEQUENCE")
    @SequenceGenerator(name = "LTL_ZONES_SEQUENCE", sequenceName = "LTL_ZONES_SEQ", allocationSize = 1)
    @Column(name = "LTL_ZONE_ID")
    private Long id;

    @Column(name = "LTL_PRIC_PROF_DETAIL_ID")
    private Long ltlPricProfDetailId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status;

    @Column(name = "COPIED_FROM")
    private Long copiedFrom;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION")
    private Long version = 1L;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "LTL_ZONE_ID", nullable = false)
    private final Set<LtlZoneGeoServicesEntity> ltlZoneGeoServicesEntities = new HashSet<LtlZoneGeoServicesEntity>();

    /**
     * Default constructor.
     */
    public LtlZonesEntity() {

    }

    /**
     * Copying constructor.
     *
     * @param source
     *            - entity to copy.
     */
    public LtlZonesEntity(LtlZonesEntity source) {
        this.copiedFrom = source.getId();
        this.name = source.getName();
        this.status = source.getStatus();
        for (LtlZoneGeoServicesEntity item : source.getLtlZoneGeoServicesEntities()) {
            this.ltlZoneGeoServicesEntities.add(new LtlZoneGeoServicesEntity(item));
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getLtlPricProfDetailId() {
        return ltlPricProfDetailId;
    }

    public void setLtlPricProfDetailId(Long ltlPricProfDetailId) {
        this.ltlPricProfDetailId = ltlPricProfDetailId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getCopiedFrom() {
        return copiedFrom;
    }

    public void setCopiedFrom(Long copiedFrom) {
        this.copiedFrom = copiedFrom;
    }

    public Set<LtlZoneGeoServicesEntity> getLtlZoneGeoServicesEntities() {
        return this.ltlZoneGeoServicesEntities;
    }

    @Override
    public PlainModificationObject getModification() {
        return this.modification;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

}

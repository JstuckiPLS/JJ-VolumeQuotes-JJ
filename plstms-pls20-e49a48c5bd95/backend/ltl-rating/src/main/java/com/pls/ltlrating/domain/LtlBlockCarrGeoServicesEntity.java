package com.pls.ltlrating.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.shared.Status;

/**
 * Ltl Block Carrier Geo Services Entity.
 *
 * @author Artem Arapov
 *
 */
@Entity
@Table(schema = "FLATBED", name = "LTL_BLOCK_CARR_GEO_SERVICES")
public class LtlBlockCarrGeoServicesEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -8437960947149722381L;

    public static final String UPDATE_STATUS_STATEMENT = "com.pls.ltlrating.domain.LtlBlockCarrGeoServicesEntity.UPDATE_STATUS_STATEMENT";

    public static final String INACTIVATE_BY_PROFILE_ID = "com.pls.ltlrating.domain.LtlBlockCarrGeoServicesEntity.INACTIVATE_BY_PROFILE_ID";

    public static final String FIND_CSP_ENTITY_BY_COPIED_FROM =
            "com.pls.ltlrating.domain.LtlBlockCarrGeoServicesEntity.FIND_CSP_ENTITY_BY_COPIED_FROM";

    public static final String UPDATE_STATUS_CHILD_CSP_STATEMENT =
            "com.pls.ltlrating.domain.LtlBlockCarrGeoServicesEntity.UPDATE_STATUS_CHILD_CSP_STATEMENT";

    public static final String INACTIVATE_CSP_BY_DETAIL_ID = "com.pls.ltlrating.domain.LtlBlockCarrGeoServicesEntity.INACTIVATE_CSP_BY_DETAIL_ID";

    public static final String DELETE_GEO_SERVICE_DTLS_BY_SERVICE_ID =
            "com.pls.ltlrating.domain.LtlBlockCarrGeoServicesEntity.DELETE_GEO_SERVICE_DTLS_BY_SERVICE_ID";

    public static final String FIND_BY_STATUS_AND_PROFILE_ID = "com.pls.ltlrating.domain.LtlBlockCarrGeoServicesEntity.FIND_BY_STATUS_AND_PROFILE_ID";


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LTL_BLOCK_CARR_GEO_SRV_SEQUENCE")
    @SequenceGenerator(name = "LTL_BLOCK_CARR_GEO_SRV_SEQUENCE", sequenceName = "LTL_BLOCK_CARR_GEO_SRV_SEQ", allocationSize = 1)
    @Column(name = "LTL_BLOCK_CARR_GEO_SERVICE_ID")
    private Long id;

    @Column(name = "LTL_PRIC_PROF_DETAIL_ID")
    private Long profileId;

    @Transient
    private String origin;

    @Transient
    private String destination;

    @Column(name = "NOTES")
    private String notes;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status;

    @Column(name = "COPIED_FROM")
    private Long copiedFrom;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "geoService")
    @Where(clause = "GEO_TYPE = 1")
    private List<LtlBlockCarrierGeoServDetailsEntity> ltlBkCarrOriginGeoServiceDetails = new ArrayList<LtlBlockCarrierGeoServDetailsEntity>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "geoService")
    @Where(clause = "GEO_TYPE = 2")
    private List<LtlBlockCarrierGeoServDetailsEntity> ltlBkCarrDestGeoServiceDetails = new ArrayList<LtlBlockCarrierGeoServDetailsEntity>();

    /**
     * Default constructor.
     */
    public LtlBlockCarrGeoServicesEntity() {

    }

    /**
     * Copying constructor.
     *
     * @param source
     *            - entity to clone.
     */
    public LtlBlockCarrGeoServicesEntity(LtlBlockCarrGeoServicesEntity source) {
        this.copiedFrom = source.getId();
        this.status = source.getStatus();
        this.notes = source.getNotes();

        for (LtlBlockCarrierGeoServDetailsEntity geoEntity : source.getLtlBkCarrOriginGeoServiceDetails()) {
            this.ltlBkCarrOriginGeoServiceDetails.add(new LtlBlockCarrierGeoServDetailsEntity(geoEntity, this));
        }

        for (LtlBlockCarrierGeoServDetailsEntity geoEntity : source.getLtlBkCarrDestGeoServiceDetails()) {
            this.ltlBkCarrDestGeoServiceDetails.add(new LtlBlockCarrierGeoServDetailsEntity(geoEntity, this));
        }
    }

    @Version
    @Column(name = "VERSION")
    private Long version = 1L;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    /**
     * Get the origin for this geo service.
     *
     * @return the origin for the geo service
     */
    public String getOrigin() {
        if (destination == null && ltlBkCarrOriginGeoServiceDetails != null && !ltlBkCarrOriginGeoServiceDetails.isEmpty()) {
            destination = StringUtils.join(ltlBkCarrOriginGeoServiceDetails, ",");
        }
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * Get the destination for the geo service.
     *
     * @return the destination for the geo service.
     */
    public String getDestination() {
        if (destination == null && ltlBkCarrDestGeoServiceDetails != null && !ltlBkCarrDestGeoServiceDetails.isEmpty()) {
            destination = StringUtils.join(ltlBkCarrDestGeoServiceDetails, ",");
        }
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public List<LtlBlockCarrierGeoServDetailsEntity> getLtlBkCarrOriginGeoServiceDetails() {
        return ltlBkCarrOriginGeoServiceDetails;
    }

    /**
     * Set the geo details for origin.
     *
     * @param ltlBkCarrOriginGeoServiceDetails
     *            Geo details to set like geo value, geo type and geo service type
     */
    public void setLtlBkCarrOriginGeoServiceDetails(List<LtlBlockCarrierGeoServDetailsEntity> ltlBkCarrOriginGeoServiceDetails) {
        this.ltlBkCarrOriginGeoServiceDetails.clear();
        this.ltlBkCarrOriginGeoServiceDetails.addAll(ltlBkCarrOriginGeoServiceDetails);
    }

    public List<LtlBlockCarrierGeoServDetailsEntity> getLtlBkCarrDestGeoServiceDetails() {
        return ltlBkCarrDestGeoServiceDetails;
    }

    /**
     * Set the geo details for destination.
     *
     * @param ltlBkCarrDestGeoServiceDetails
     *            Geo details to set like geo value, geo type and geo service type
     */
    public void setLtlBkCarrDestGeoServiceDetails(List<LtlBlockCarrierGeoServDetailsEntity> ltlBkCarrDestGeoServiceDetails) {
        this.ltlBkCarrDestGeoServiceDetails.clear();
        this.ltlBkCarrDestGeoServiceDetails.addAll(ltlBkCarrDestGeoServiceDetails);
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (this == obj) {
            result = true;
        } else if (obj instanceof LtlBlockCarrGeoServicesEntity) {
            LtlBlockCarrGeoServicesEntity rhs = (LtlBlockCarrGeoServicesEntity) obj;
            EqualsBuilder builder = new EqualsBuilder();

            result = builder.append(this.getOrigin(), rhs.getOrigin())
                    .append(this.getDestination(), rhs.getDestination()).isEquals();
        }

        return result;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        return builder.append(this.getOrigin()).append(this.getDestination()).hashCode();
    }
}

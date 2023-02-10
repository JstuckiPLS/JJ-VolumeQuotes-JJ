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
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

/**
 * Ltl zone geo services.
 *
 * @author Artem Arapov
 *
 */
@Entity
@Table(name = "LTL_ZONE_GEO_SERVICES")
public class LtlZoneGeoServicesEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -5625092927322080320L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LTL_ZONE_GEO_SERVICES_SEQUENCE")
    @SequenceGenerator(name = "LTL_ZONE_GEO_SERVICES_SEQUENCE", sequenceName = "LTL_ZONE_GEO_SERVICES_SEQ", allocationSize = 1)
    @Column(name = "LTL_ZONE_GEO_SERVICE_ID")
    private Long id;

    @Transient
    private String location;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION", nullable = false)
    private Long version = 1L;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "LTL_ZONE_GEO_SERVICE_ID", nullable = false)
    private List<LtlZoneGeoServiceDetailsEntity> ltlZoneGeoServiceDetails = new ArrayList<LtlZoneGeoServiceDetailsEntity>();


    /**
     * Default constructor.
     */
    public LtlZoneGeoServicesEntity() {

    }

    /**
     * Copying constructor.
     *
     * @param source
     *            - entity to copy.
     */
    public LtlZoneGeoServicesEntity(LtlZoneGeoServicesEntity source) {
        for (LtlZoneGeoServiceDetailsEntity geoEntity : source.getLtlZoneGeoServiceDetails()) {
            this.ltlZoneGeoServiceDetails.add(new LtlZoneGeoServiceDetailsEntity(geoEntity));
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

    /**
     * Get the location for the geo service.
     *
     * @return the location for the geo service.
     */
    public String getLocation() {
        if (location == null && ltlZoneGeoServiceDetails != null && !ltlZoneGeoServiceDetails.isEmpty()) {
            location = StringUtils.join(ltlZoneGeoServiceDetails, ",");
        }
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public List<LtlZoneGeoServiceDetailsEntity> getLtlZoneGeoServiceDetails() {
        return ltlZoneGeoServiceDetails;
    }

    /**
     * Set the geo details.
     *
     * @param ltlZoneGeoServiceDetails
     *            Geo details to set like geo value, geo type and geo service type
     */
    public void setLtlZoneGeoServiceDetails(List<LtlZoneGeoServiceDetailsEntity> ltlZoneGeoServiceDetails) {
        this.ltlZoneGeoServiceDetails.clear();
        this.ltlZoneGeoServiceDetails.addAll(ltlZoneGeoServiceDetails);
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getLocation());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if ((obj instanceof LtlZoneGeoServicesEntity)) {
            if (obj == this) {
                result = true;
            } else {
                LtlZoneGeoServicesEntity other = (LtlZoneGeoServicesEntity) obj;
                EqualsBuilder builder = new EqualsBuilder();
                builder.append(getLocation(), other.getLocation());

                result = builder.isEquals();
            }
        }
        return result;
    }
}

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
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

/**
 * LtlFuelGeoServicesEntity.
 *
 * @author Stas Norochevskiy
 *
 */
@Entity
@Table(name = "LTL_FUEL_GEO_SERVICES")
public class LtlFuelGeoServicesEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ltl_fuel_geo_serv_seq")
    @SequenceGenerator(name = "ltl_fuel_geo_serv_seq",
                       sequenceName = "LTL_FUEL_GEO_SERVICES_SEQ", allocationSize = 1)
    @Column(name = "LTL_FUEL_GEO_SERVICE_ID")
    private Long id;

    @Column(name = "LTL_FUEL_ID", updatable = false, insertable = false)
    private Long ltlFuelId;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Transient
    private String origin;

    @Version
    @Column(name = "VERSION")
    private Long version = 1L;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "LTL_FUEL_GEO_SERVICE_ID", nullable = false)
    private List<LtlFuelGeoServiceDetailsEntity> ltlFuelGeoServiceDetails = new ArrayList<LtlFuelGeoServiceDetailsEntity>();

    /**
     * Default Constructor.
     */
    public LtlFuelGeoServicesEntity() {
    }

    /**
     * Copying constructor.
     *
     * @param source
     *          Entity to be cloned.
     */
    public LtlFuelGeoServicesEntity(LtlFuelGeoServicesEntity source) {
        for (LtlFuelGeoServiceDetailsEntity geoEntity : source.getLtlFuelGeoServiceDetails()) {
            this.ltlFuelGeoServiceDetails.add(new LtlFuelGeoServiceDetailsEntity(geoEntity));
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
     * Get the origin for this geo service.
     *
     * @return the origin for the geo service
     */
    public String getOrigin() {
        if (origin == null && ltlFuelGeoServiceDetails != null && !ltlFuelGeoServiceDetails.isEmpty()) {
            origin = StringUtils.join(ltlFuelGeoServiceDetails, ",");
        }
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Long getLtlFuelId() {
        return ltlFuelId;
    }

    public PlainModificationObject getModification() {
        return modification;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public List<LtlFuelGeoServiceDetailsEntity> getLtlFuelGeoServiceDetails() {
        return ltlFuelGeoServiceDetails;
    }

    /**
     * Set the geo details.
     *
     * @param ltlFuelGeoServiceDetails
     *            Geo details to set like geo value, geo type and geo service type
     */
    public void setLtlFuelGeoServiceDetails(List<LtlFuelGeoServiceDetailsEntity> ltlFuelGeoServiceDetails) {
        this.ltlFuelGeoServiceDetails.clear();
        this.ltlFuelGeoServiceDetails.addAll(ltlFuelGeoServiceDetails);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.getOrigin()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LtlFuelGeoServicesEntity other = (LtlFuelGeoServicesEntity) obj;
        return new EqualsBuilder().append(this.getOrigin(), other.getOrigin()).isEquals();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("origin", getOrigin()).append("modification", getModification());

        return builder.toString();
    }
}

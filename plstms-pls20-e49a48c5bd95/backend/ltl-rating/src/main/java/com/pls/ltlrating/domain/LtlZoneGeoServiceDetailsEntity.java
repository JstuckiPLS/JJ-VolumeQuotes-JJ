package com.pls.ltlrating.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

/**
 * LtlZoneGeoServiceDetailsEntity.
 *
 * @author Pavani Challa
 *
 */
@Entity
@Table(name = "LTL_ZONE_GEO_SERV_DTLS")
public class LtlZoneGeoServiceDetailsEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ltl_zone_geo_serv_dtl_seq")
    @SequenceGenerator(name = "ltl_zone_geo_serv_dtl_seq",
                       sequenceName = "LTL_ZONE_GEO_SERV_DTL_SEQ", allocationSize = 1)
    @Column(name = "LTL_ZONE_GEO_SERV_DTL_ID")
    private Long id;

    @Column(name = "LTL_ZONE_GEO_SERVICE_ID", updatable = false, insertable = false)
    private Long ltlZoneGeoServiceId;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Column(name = "GEO_VALUE", nullable = false)
    private String geoValue;

    @Column(name = "GEO_SERV_TYPE", nullable = false)
    private int geoServType;

    @Column(name = "SEARCHABLE_GEO_VALUE", nullable = false)
    private String geoValueSearchable;

    @Version
    @Column(name = "VERSION")
    private Long version = 1L;

    /**
     * Default Constructor.
     */
    public LtlZoneGeoServiceDetailsEntity() {
    }

    /**
     * Constructor with parameters.
     *
     * @param geoServiceId
     *            Id of the geo service for which this entity is child of
     * @param geoValue
     *            geo code for this detail
     * @param geoServType
     *            geo service type for the geo code
     * @param geoValueSearchable
     *            geo values ASCII equivalent.
     */
    public LtlZoneGeoServiceDetailsEntity(Long geoServiceId, String geoValue, int geoServType, String geoValueSearchable) {
        this.ltlZoneGeoServiceId = geoServiceId;
        this.geoValue = geoValue;
        this.geoServType = geoServType;
        this.geoValueSearchable = geoValueSearchable;
    }

    /**
     * Copying constructor.
     *
     * @param source
     *          Entity to be cloned.
     */
    public LtlZoneGeoServiceDetailsEntity(LtlZoneGeoServiceDetailsEntity source) {
        this.geoValue = source.getGeoValue();
        this.geoServType = source.getGeoServType();
        this.geoValueSearchable = source.getGeoValueSearchable();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getLtlZoneGeoServiceId() {
        return ltlZoneGeoServiceId;
    }

    public void setLtlZoneGeoServiceId(Long ltlZoneGeoServiceId) {
        this.ltlZoneGeoServiceId = ltlZoneGeoServiceId;
    }

    public String getGeoValue() {
        return geoValue;
    }

    public void setGeoValue(String geoValue) {
        this.geoValue = geoValue;
    }

    public int getGeoServType() {
        return geoServType;
    }

    public void setGeoServType(int geoServType) {
        this.geoServType = geoServType;
    }

    public String getGeoValueSearchable() {
        return geoValueSearchable;
    }

    public PlainModificationObject getModification() {
        return modification;
    }

    public void setGeoValueSearchable(String geoValueSearchable) {
        this.geoValueSearchable = geoValueSearchable;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.geoValue).append(this.geoServType).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LtlZoneGeoServiceDetailsEntity other = (LtlZoneGeoServiceDetailsEntity) obj;
        return new EqualsBuilder().append(this.geoValue, other.geoValue).append(this.geoServType, other.geoServType).isEquals();
    }

    @Override
    public String toString() {
        return this.geoValue;
    }
}

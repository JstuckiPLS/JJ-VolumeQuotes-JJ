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
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.ltlrating.domain.enums.GeoType;

/**
 * LtlFuelGeoServiceDetailsEntity.
 *
 * @author Pavani Challa
 *
 */
@Entity
@Table(name = "LTL_FUEL_GEO_SERV_DTLS")
public class LtlFuelGeoServiceDetailsEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ltl_fuel_geo_serv_dtl_seq")
    @SequenceGenerator(name = "ltl_fuel_geo_serv_dtl_seq",
                       sequenceName = "LTL_FUEL_GEO_SERV_DTL_SEQ", allocationSize = 1)
    @Column(name = "LTL_FUEL_GEO_SERV_DTL_ID")
    private Long id;

    @Column(name = "LTL_FUEL_GEO_SERVICE_ID", updatable = false, insertable = false)
    private Long ltlFuelGeoServiceId;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Column(name = "GEO_VALUE", nullable = false)
    private String geoValue;

    @Column(name = "GEO_SERV_TYPE", nullable = false)
    private int geoServType;

    @Column(name = "GEO_TYPE", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.ltlrating.domain.enums.GeoType"),
            @Parameter(name = "identifierMethod", value = "getType"),
            @Parameter(name = "valueOfMethod", value = "getGeoTypeBy") })
    private GeoType geoType;

    @Column(name = "SEARCHABLE_GEO_VALUE", nullable = false)
    private String geoValueSearchable;

    @Version
    @Column(name = "VERSION")
    private Long version = 1L;

    /**
     * Default Constructor.
     */
    public LtlFuelGeoServiceDetailsEntity() {
    }

    /**
     * Constructor with parameters.
     *
     * @param geoServiceId
     *            Id of the geo service for which this entity is child of
     * @param geoValue
     *            geo code for this detail
     * @param geoType
     *            geo type - whether its origin/destination
     * @param geoServType
     *            geo service type for the geo code
     * @param geoValueSearchable
     *            geo value searchable
     */
    public LtlFuelGeoServiceDetailsEntity(Long geoServiceId, String geoValue, GeoType geoType, int geoServType,
            String geoValueSearchable) {
        this.ltlFuelGeoServiceId = geoServiceId;
        this.geoValue = geoValue;
        this.geoType = geoType;
        this.geoServType = geoServType;
        this.geoValueSearchable = geoValueSearchable;
    }

    /**
     * Copying constructor.
     *
     * @param source
     *          Entity to be cloned.
     */
    public LtlFuelGeoServiceDetailsEntity(LtlFuelGeoServiceDetailsEntity source) {
        this.geoValue = source.getGeoValue();
        this.geoType = source.getGeoType();
        this.geoServType = source.getGeoServType();
        this.geoValueSearchable = source.getGeoValueSearchable();
    }

    public String getGeoValueSearchable() {
        return geoValueSearchable;
    }

    public void setGeoValueSearchable(String geoValueSearchable) {
        this.geoValueSearchable = geoValueSearchable;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getLtlFuelGeoServiceId() {
        return ltlFuelGeoServiceId;
    }

    public void setLtlFuelGeoServiceId(Long ltlFuelGeoServiceId) {
        this.ltlFuelGeoServiceId = ltlFuelGeoServiceId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setGeoValue(String geoValue) {
        this.geoValue = geoValue;
    }

    public String getGeoValue() {
        return geoValue;
    }

    public void setGeoServType(int geoServType) {
        this.geoServType = geoServType;
    }

    public int getGeoServType() {
        return geoServType;
    }

    public PlainModificationObject getModification() {
        return modification;
    }

    public void setGeoType(GeoType geoType) {
        this.geoType = geoType;
    }

    public GeoType getGeoType() {
        return geoType;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.geoValue).append(this.geoType).append(this.geoServType).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LtlFuelGeoServiceDetailsEntity other = (LtlFuelGeoServiceDetailsEntity) obj;
        return new EqualsBuilder().append(this.geoValue, other.geoValue).append(this.geoType, other.geoType)
                .append(this.geoServType, other.geoServType).isEquals();
    }

    @Override
    public String toString() {
        return this.geoValue;
    }
}

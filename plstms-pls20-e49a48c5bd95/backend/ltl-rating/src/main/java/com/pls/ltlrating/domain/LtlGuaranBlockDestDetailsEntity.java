package com.pls.ltlrating.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.ltlrating.domain.enums.GeoType;

/**
 * LtlGuaranBlockDestDetailsEntity.
 *
 * @author Pavani Challa
 *
 */
@Entity
@Table(name = "LTL_GUARAN_BLOCK_DEST_DTLS")
public class LtlGuaranBlockDestDetailsEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ltl_guaran_bk_dest_dtl_seq")
    @SequenceGenerator(name = "ltl_guaran_bk_dest_dtl_seq",
                       sequenceName = "LTL_GUARAN_BK_DEST_DTL_SEQ", allocationSize = 1)
    @Column(name = "LTL_GUARAN_BK_DEST_DTL_ID")
    private Long id;

    @Column(name = "LTL_GUARAN_BLOCK_DEST_ID", nullable = false, insertable = false, updatable = false)
    private Long ltlGuaranBlockDestId;

    @Column(name = "GEO_VALUE", nullable = false)
    private String geoValue;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LTL_GUARAN_BLOCK_DEST_ID", nullable = false)
    @JsonIgnore
    private LtlGuaranteedBlockDestEntity geoService;

    @Column(name = "GEO_TYPE", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.ltlrating.domain.enums.GeoType"),
            @Parameter(name = "identifierMethod", value = "getType"),
            @Parameter(name = "valueOfMethod", value = "getGeoTypeBy") })
    private GeoType geoType;

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
    public LtlGuaranBlockDestDetailsEntity() {
    }

    /**
     * Constructor with parameters.
     *
     * @param geoService
     *            geo service for which this entity is a child of
     * @param geoValue
     *            geo code for this detail
     * @param geoType
     *            geo type - whether its origin/destination
     * @param geoServType
     *            geo service type for the geo code
     * @param geoValueSearchable
     *            geo value searchable
     */
    public LtlGuaranBlockDestDetailsEntity(LtlGuaranteedBlockDestEntity geoService, String geoValue, GeoType geoType, int geoServType,
            String geoValueSearchable) {
        this.geoService = geoService;
        this.geoValue = geoValue;
        this.geoType = geoType;
        this.geoServType = geoServType;
        this.ltlGuaranBlockDestId = geoService.getId();
        this.geoValueSearchable = geoValueSearchable;
    }

    /**
     * Copying constructor.
     *
     * @param source
     *            Entity to be cloned.
     * @param geoService
     *            geo service for which this entity is child of
     */
    public LtlGuaranBlockDestDetailsEntity(LtlGuaranBlockDestDetailsEntity source, LtlGuaranteedBlockDestEntity geoService) {
        this.geoValue = source.getGeoValue();
        this.geoType = source.getGeoType();
        this.geoServType = source.getGeoServType();
        this.geoService = geoService;
        this.ltlGuaranBlockDestId = geoService.getId();
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

    public Long getLtlGuaranBlockDestId() {
        return ltlGuaranBlockDestId;
    }

    public void setLtlGuaranBlockDestId(Long ltlGuaranBlockDestId) {
        this.ltlGuaranBlockDestId = ltlGuaranBlockDestId;
    }

    public void setGeoValue(String geoValue) {
        this.geoValue = geoValue;
    }

    public String getGeoValue() {
        return geoValue;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setGeoType(GeoType geoType) {
        this.geoType = geoType;
    }

    public GeoType getGeoType() {
        return geoType;
    }

    public void setGeoServType(int geoServType) {
        this.geoServType = geoServType;
    }

    public int getGeoServType() {
        return geoServType;
    }

    public String getGeoValueSearchable() {
        return geoValueSearchable;
    }

    public void setGeoValueSearchable(String geoValueSearchable) {
        this.geoValueSearchable = geoValueSearchable;
    }

    public PlainModificationObject getModification() {
        return modification;
    }

    public LtlGuaranteedBlockDestEntity getGeoService() {
        return geoService;
    }

    public void setGeoService(LtlGuaranteedBlockDestEntity geoService) {
        this.geoService = geoService;
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
        final LtlGuaranBlockDestDetailsEntity other = (LtlGuaranBlockDestDetailsEntity) obj;
        return new EqualsBuilder().append(this.geoValue, other.geoValue).append(this.geoType, other.geoType)
                .append(this.geoServType, other.geoServType).isEquals();
    }

    @Override
    public String toString() {
        return this.geoValue;
    }
}

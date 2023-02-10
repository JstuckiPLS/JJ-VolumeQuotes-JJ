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

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.ltlrating.domain.enums.GeoType;

/**
 * Entity containing origin and destination details regarding Blocking lanes for specific/all blanket carrier
 * profiles for a customer.
 *
 * @author Ashwini Neelgund
 *
 */
@Entity
@Table(name = "LTL_BK_LANE_GEO_SERV_DTLS")
public class LtlBlockLaneGeoServDetailsEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -1241902902463392666L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LTL_BK_LANE_GEO_SERV_DTL_SEQ")
    @SequenceGenerator(name = "LTL_BK_LANE_GEO_SERV_DTL_SEQ", sequenceName = "LTL_BK_LANE_GEO_SERV_DTL_SEQ", allocationSize = 1)
    @Column(name = "LTL_BK_LANE_GEO_SERV_DTL_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LTL_BLOCK_LANE_ID", nullable = false)
    private LtlBlockLaneEntity blockLane;

    @Column(name = "GEO_TYPE", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.ltlrating.domain.enums.GeoType"),
            @Parameter(name = "identifierMethod", value = "getType"),
            @Parameter(name = "valueOfMethod", value = "getGeoTypeBy") })
    private GeoType geoType;

    @Column(name = "GEO_VALUE", nullable = false)
    private String geoValue;

    @Column(name = "GEO_SERV_TYPE", nullable = false)
    private int geoServType;

    @Column(name = "SEARCHABLE_GEO_VALUE", nullable = false)
    private String geoValueSearchable;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    /**
     * Default Constructor.
     */
    public LtlBlockLaneGeoServDetailsEntity() {
    }

    /**
     * Constructor with parameters.
     *
     * @param blockLane
     *            block lane
     * @param geoValue
     *            geo code for this detail
     * @param geoType
     *            geo type - whether its origin/destination
     * @param geoServType
     *            geo service type for the geo code
     * @param geoValueSearchable
     *            geo value searchable
     */
    public LtlBlockLaneGeoServDetailsEntity(LtlBlockLaneEntity blockLane, String geoValue,
            GeoType geoType, int geoServType, String geoValueSearchable) {
        this.blockLane = blockLane;
        this.geoValue = geoValue;
        this.geoType = geoType;
        this.geoServType = geoServType;
        this.geoValueSearchable = geoValueSearchable;
    }

    @Version
    @Column(name = "VERSION")
    private Long version = 1L;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public LtlBlockLaneEntity getBlockLane() {
        return blockLane;
    }

    public void setBlockLane(LtlBlockLaneEntity blockLane) {
        this.blockLane = blockLane;
    }

    public String getGeoValue() {
        return geoValue;
    }

    public void setGeoValue(String geoValue) {
        this.geoValue = geoValue;
    }

    public GeoType getGeoType() {
        return geoType;
    }

    public void setGeoType(GeoType geoType) {
        this.geoType = geoType;
    }

    public String getGeoValueSearchable() {
        return geoValueSearchable;
    }

    public void setGeoValueSearchable(String geoValueSearchable) {
        this.geoValueSearchable = geoValueSearchable;
    }

    public int getGeoServType() {
        return geoServType;
    }

    public void setGeoServType(int geoServType) {
        this.geoServType = geoServType;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public PlainModificationObject getModification() {
        return modification;
    }

    @Override
    public String toString() {
        return this.geoValue;
    }
}

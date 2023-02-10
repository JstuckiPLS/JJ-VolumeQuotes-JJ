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
 * LtlPricGeoServiceDetailsEntity.
 *
 * @author Pavani Challa
 *
 */
@Entity
@Table(name = "LTL_PRIC_GEO_SERV_DTLS")
public class LtlPricGeoServiceDetailsEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ltl_pric_geo_serv_dtl_seq")
    @SequenceGenerator(name = "ltl_pric_geo_serv_dtl_seq",
                       sequenceName = "LTL_PRIC_GEO_SERV_DTL_SEQ", allocationSize = 1)
    @Column(name = "LTL_PRIC_GEO_SERV_DTL_ID")
    private Long id;

    @Column(name = "GEO_VALUE", nullable = false)
    private String geoValue;

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

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION")
    private Long version = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LTL_PRICING_GEO_SERVICE_ID", nullable = false)
    private LtlPricingGeoServicesEntity geoService;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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

    public int getGeoServType() {
        return geoServType;
    }

    public void setGeoServType(int geoServType) {
        this.geoServType = geoServType;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public LtlPricingGeoServicesEntity getGeoService() {
        return geoService;
    }

    public void setGeoService(LtlPricingGeoServicesEntity geoService) {
        this.geoService = geoService;
    }
}

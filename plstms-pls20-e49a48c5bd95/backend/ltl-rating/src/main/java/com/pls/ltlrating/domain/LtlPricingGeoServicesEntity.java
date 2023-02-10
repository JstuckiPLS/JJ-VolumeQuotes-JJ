package com.pls.ltlrating.domain;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Where;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

/**
 * Ltl Pricing Geo Services.
 *
 * @author Artem Arapov
 *
 */
@Entity
@Table(name = "LTL_PRICING_GEO_SERVICES", schema = "FLATBED")
public class LtlPricingGeoServicesEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 7139433561538877591L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LTL_PRICE_GEO_SERVICE_SEQUENCE")
    @SequenceGenerator(name = "LTL_PRICE_GEO_SERVICE_SEQUENCE", sequenceName = "LTL_PRIC_GEO_SRV_SEQ", allocationSize = 1)
    @Column(name = "LTL_PRICING_GEO_SERVICE_ID")
    private Long id;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Long version = 1L;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "geoService")
    @Where(clause = "GEO_TYPE = 1")
    private Set<LtlPricGeoServiceDetailsEntity> originDetails;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "geoService")
    @Where(clause = "GEO_TYPE = 2")
    private Set<LtlPricGeoServiceDetailsEntity> destinationDetails;

    @ManyToOne
    @JoinColumn(name = "LTL_PRICING_DETAIL_ID", nullable = false)
    private LtlPricingDetailsEntity pricingDetail;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public Set<LtlPricGeoServiceDetailsEntity> getOriginDetails() {
        return originDetails;
    }

    public void setOriginDetails(Set<LtlPricGeoServiceDetailsEntity> originDetails) {
        this.originDetails = originDetails;
    }

    public Set<LtlPricGeoServiceDetailsEntity> getDestinationDetails() {
        return destinationDetails;
    }

    public void setDestinationDetails(Set<LtlPricGeoServiceDetailsEntity> destinationDetails) {
        this.destinationDetails = destinationDetails;
    }

    public LtlPricingDetailsEntity getPricingDetail() {
        return pricingDetail;
    }

    public void setPricingDetail(LtlPricingDetailsEntity pricingDetail) {
        this.pricingDetail = pricingDetail;
    }
}

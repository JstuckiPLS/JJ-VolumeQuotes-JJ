package com.pls.ltlrating.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.CommodityClass;

/**
 * Ltl Freight All Kinds Map.
 *
 * @author Artem Arapov
 */
@Entity
@Table(name = "LTL_FAK_MAP")
public class LtlFakMapEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -7334493662712221112L;

    public static final String FIND_BY_PRICING_DETAIL_ID = "com.pls.ltlrating.domain.LtlFakMapEntity.FIND_BY_PRICING_DETAIL_ID";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LTL_FAK_MAP_SEQUENCE")
    @SequenceGenerator(name = "LTL_FAK_MAP_SEQUENCE", sequenceName = "LTL_FAK_MAP_SEQ", allocationSize = 1)
    @Column(name = "LTL_FAK_MAP_ID")
    private Long id;

    @Column(name = "ACTUAL_CLASS")
    @Enumerated(EnumType.STRING)
    private CommodityClass actualClass;

    @Column(name = "MAPPING_CLASS")
    @Enumerated(EnumType.STRING)
    private CommodityClass mappingClass;

    @ManyToOne
    @JoinColumn(name = "LTL_PRICING_DETAIL_ID", nullable = false)
    private LtlPricingDetailsEntity pricingDetail;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

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

    public CommodityClass getActualClass() {
        return actualClass;
    }

    public void setActualClass(CommodityClass actualClass) {
        this.actualClass = actualClass;
    }

    public CommodityClass getMappingClass() {
        return mappingClass;
    }

    public void setMappingClass(CommodityClass mappingClass) {
        this.mappingClass = mappingClass;
    }

    public LtlPricingDetailsEntity getPricingDetail() {
        return pricingDetail;
    }

    public void setPricingDetail(LtlPricingDetailsEntity pricingDetail) {
        this.pricingDetail = pricingDetail;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}

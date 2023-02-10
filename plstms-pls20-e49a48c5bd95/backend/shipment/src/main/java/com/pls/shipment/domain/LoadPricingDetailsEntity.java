package com.pls.shipment.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.HasVersion;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.ltlrating.domain.enums.MoveType;
import com.pls.ltlrating.domain.enums.PricingType;

/**
 * Contains carrier specific pricing details for selected proposition.
 *
 * @author Ashwini Neelgund
 */
@Entity
@Table(name = "LOAD_PRICING_DETAILS")
public class LoadPricingDetailsEntity implements Identifiable<Long>, HasModificationInfo, HasVersion {

    private static final long serialVersionUID = -360556966399648449L;

    public static final String Q_LOAD_PRICING_DETAILS = "com.pls.shipment.domain.LoadPricingDetailsEntity.Q_LOAD_PRICING_DETAILS";
    public static final String Q_DELETE_PRICING_DETAIL_BY_ID = "com.pls.shipment.domain.LoadPricingDetailsEntity.Q_DELETE_PRICING_DETAIL_BY_ID";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "load_pricing_details_item_sequence")
    @SequenceGenerator(name = "load_pricing_details_item_sequence", sequenceName = "LOAD_PRICING_DETAILS_SEQ", allocationSize = 1)
    @Column(name = "LOAD_PRICING_DETAIL_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAD_ID", nullable = false)
    private LoadEntity load;

    @Column(name = "SMC3_MINIMUM_CHARGE")
    private BigDecimal smc3MinimumCharge;

    @Column(name = "TOTAL_CHARGE_FROM_SMC3")
    private BigDecimal totalChargeFromSmc3;

    @Column(name = "DEFICIT_CHARGE_FROM_SMC3")
    private BigDecimal deficitChargeFromSmc3;

    @Column(name = "COST_AFTER_DISCOUNT")
    private BigDecimal costAfterDiscount;

    @Column(name = "MINIMUM_COST")
    private BigDecimal minimumCost;

    @Column(name = "COST_DISCOUNT")
    private BigDecimal costDiscount;

    @Column(name = "CARRIER_FS_ID")
    private Long carrierFSId;

    @Column(name = "CARRIER_FUEL_DISCOUNT")
    private BigDecimal carrierFuelDiscount;

    @Column(name = "PRICING_TYPE")
    @Enumerated(EnumType.STRING)
    private PricingType pricingType;

    @Column(name = "MOVEMENT_TYPE")
    @Enumerated(EnumType.STRING)
    private MoveType movementType;

    @Column(name = "EFFECTIVE_DATE")
    private Date effectiveDate;

    @OneToMany(mappedBy = "loadPricingDetails", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<LoadPricMaterialDtlsEntity> loadPricMaterialDtls;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoadEntity getLoad() {
        return load;
    }

    public void setLoad(LoadEntity load) {
        this.load = load;
    }

    public BigDecimal getSmc3MinimumCharge() {
        return smc3MinimumCharge;
    }

    public void setSmc3MinimumCharge(BigDecimal smc3MinimumCharge) {
        this.smc3MinimumCharge = smc3MinimumCharge;
    }

    public BigDecimal getTotalChargeFromSmc3() {
        return totalChargeFromSmc3;
    }

    public void setTotalChargeFromSmc3(BigDecimal totalChargeFromSmc3) {
        this.totalChargeFromSmc3 = totalChargeFromSmc3;
    }

    public BigDecimal getDeficitChargeFromSmc3() {
        return deficitChargeFromSmc3;
    }

    public void setDeficitChargeFromSmc3(BigDecimal deficitChargeFromSmc3) {
        this.deficitChargeFromSmc3 = deficitChargeFromSmc3;
    }

    public BigDecimal getCostAfterDiscount() {
        return costAfterDiscount;
    }

    public void setCostAfterDiscount(BigDecimal costAfterDiscount) {
        this.costAfterDiscount = costAfterDiscount;
    }

    public BigDecimal getMinimumCost() {
        return minimumCost;
    }

    public void setMinimumCost(BigDecimal minimumCost) {
        this.minimumCost = minimumCost;
    }

    public BigDecimal getCostDiscount() {
        return costDiscount;
    }

    public void setCostDiscount(BigDecimal costDiscount) {
        this.costDiscount = costDiscount;
    }

    public Long getCarrierFSId() {
        return carrierFSId;
    }

    public void setCarrierFSId(Long carrierFSId) {
        this.carrierFSId = carrierFSId;
    }

    public BigDecimal getCarrierFuelDiscount() {
        return carrierFuelDiscount;
    }

    public void setCarrierFuelDiscount(BigDecimal carrierFuelDiscount) {
        this.carrierFuelDiscount = carrierFuelDiscount;
    }

    public PricingType getPricingType() {
        return pricingType;
    }

    public void setPricingType(PricingType pricingType) {
        this.pricingType = pricingType;
    }

    public MoveType getMovementType() {
        return movementType;
    }

    public void setMovementType(MoveType movementType) {
        this.movementType = movementType;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Set<LoadPricMaterialDtlsEntity> getLoadPricMaterialDtls() {
        return loadPricMaterialDtls;
    }

    public void setLoadPricMaterialDtls(Set<LoadPricMaterialDtlsEntity> loadPricMaterialDtls) {
        this.loadPricMaterialDtls = loadPricMaterialDtls;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}

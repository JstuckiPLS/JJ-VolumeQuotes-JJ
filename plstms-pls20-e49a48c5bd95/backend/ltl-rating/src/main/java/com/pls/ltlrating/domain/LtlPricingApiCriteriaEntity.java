package com.pls.ltlrating.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.ltlrating.domain.enums.MoveType;

/**
 *
 * Entity object for the API criteria.
 *
 * @author Pavani Challa
 *
 */
@Entity
@Table(name = "LTL_PRICING_API_CRIT")
public class LtlPricingApiCriteriaEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 1542701514194626625L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ltl_pricing_api_crit_seq")
    @SequenceGenerator(name = "ltl_pricing_api_crit_seq", sequenceName = "ltl_pricing_api_crit_seq", allocationSize = 1)
    @Column(name = "LTL_PRICING_API_CRIT_ID")
    private Long id;

    @Column(name = "SHIPPER_ORG_ID")
    private Long shipperOrgId;

    @Column(name = "CARRIER_ORG_ID")
    private Long carrierOrgId;

    @Column(name = "SHIP_DATE")
    private Date shipDate;

    @Column(name = "ACCESSORIAL_TYPES")
    private String accessorialTypes;

    @Column(name = "GUARANTEED_TIME")
    private Integer guaranteedTime;

    @Column(name = "PALLET_TYPE")
    private String palletType;

    @Column(name = "GAINSHARE_ACCOUNT")
    private String gainShareAccount;

    @Column(name = "MOVEMENT_TYPE")
    @Enumerated(EnumType.STRING)
    private MoveType movementType;

    @Column(name = "API_RETURN_TIME")
    private BigDecimal apiReturnTime;

    @Column(name = "USERID")
    private String userId;

    @Column(name = "REQUEST_TYPE")
    private String requestType;

    @Column(name = "SCAC")
    private String scac;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION")
    private Long version = 1L;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "LTL_PRICING_API_CRIT_ID", nullable = false)
    private List<LtlPricingApiMaterialsCritEntity> loadMaterials;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "ORIGIN", nullable = false, referencedColumnName = "LTL_PRICING_API_ADDRESS_ID")
    private LtlPricingApiAddressCritEntity origin;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DESTINATION", nullable = false, referencedColumnName = "LTL_PRICING_API_ADDRESS_ID")
    private LtlPricingApiAddressCritEntity destination;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShipperOrgId() {
        return shipperOrgId;
    }

    public void setShipperOrgId(Long shipperOrgId) {
        this.shipperOrgId = shipperOrgId;
    }

    public Long getCarrierOrgId() {
        return carrierOrgId;
    }

    public void setCarrierOrgId(Long carrierOrgId) {
        this.carrierOrgId = carrierOrgId;
    }

    public Date getShipDate() {
        return shipDate;
    }

    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }

    public String getAccessorialTypes() {
        return accessorialTypes;
    }

    public void setAccessorialTypes(String accessorialTypes) {
        this.accessorialTypes = accessorialTypes;
    }

    public Integer getGuaranteedTime() {
        return guaranteedTime;
    }

    public void setGuaranteedTime(Integer guaranteedTime) {
        this.guaranteedTime = guaranteedTime;
    }

    public String getPalletType() {
        return palletType;
    }

    public void setPalletType(String palletType) {
        this.palletType = palletType;
    }

    public String getGainShareAccount() {
        return gainShareAccount;
    }

    public void setGainShareAccount(String gainShareAccount) {
        this.gainShareAccount = gainShareAccount;
    }

    public MoveType getMovementType() {
        return movementType;
    }

    public void setMovementType(MoveType movementType) {
        this.movementType = movementType;
    }

    public BigDecimal getApiReturnTime() {
        return apiReturnTime;
    }

    public void setApiReturnTime(BigDecimal apiReturnTime) {
        this.apiReturnTime = apiReturnTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public List<LtlPricingApiMaterialsCritEntity> getLoadMaterials() {
        return loadMaterials;
    }

    public void setLoadMaterials(List<LtlPricingApiMaterialsCritEntity> loadMaterials) {
        this.loadMaterials = loadMaterials;
    }

    public PlainModificationObject getModification() {
        return modification;
    }

    public LtlPricingApiAddressCritEntity getOrigin() {
        return origin;
    }

    public void setOrigin(LtlPricingApiAddressCritEntity origin) {
        this.origin = origin;
    }

    public LtlPricingApiAddressCritEntity getDestination() {
        return destination;
    }

    public void setDestination(LtlPricingApiAddressCritEntity destination) {
        this.destination = destination;
    }
}

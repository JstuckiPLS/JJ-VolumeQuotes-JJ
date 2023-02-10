package com.pls.shipment.domain;

import java.math.BigDecimal;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CarrierEntity;

/**
 * Cost details Entity.
 * 
 * @author Gleb Zgonikov
 */
@Entity
@Table(name = "RATER.COST_DETAIL_ITEMS")
public class CostDetailItemEntity implements Identifiable<Long>, HasModificationInfo {

    public static final String Q_GET_BILL_TO_FOR_AUTOMATIC_PROCESSING =
            "com.pls.shipment.domain.CostDetailItemEntity.Q_GET_BILL_TO_FOR_AUTOMATIC_PROCESSING";
    public static final String Q_GET_LOAD_JOB_NUMS_AND_FRT_CLASS =
            "com.pls.shipment.domain.CostDetailItemEntity.Q_GET_LOAD_JOB_NUMS_AND_FRT_CLASS";
    public static final String Q_GET_LOAD_JOB_NUMS_AND_FRT_CLASS_ADJ =
            "com.pls.shipment.domain.CostDetailItemEntity.Q_GET_LOAD_JOB_NUMS_AND_FRT_CLASS_ADJ";

    private static final long serialVersionUID = -5586947557543561620L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cost_details_item_sequence")
    @SequenceGenerator(name = "cost_details_item_sequence", sequenceName = "RATER.CDI_SEQ", allocationSize = 1)
    @Column(name = "ITEM_ID")
    private Long id;

    @Column(name = "SUBTOTAL", columnDefinition = "NUMBER(10,2)")
    private BigDecimal subtotal;

    @Column(name = "UNIT_COST")
    private BigDecimal unitCost;

    /**
     * Not nullable field.
     */
    @Column(name = "REF_ID")
    private Long refId = -1L;

    @Column(name = "DEDICATED_UNIT_ID")
    private Long dedicatedUnitId = -1L;

    @Column(name = "RATE_INDICATOR")
    private String rateIndicator = "O";

    @Column(name = "QUANTITY")
    private Long quantity = 1L;

    @Column(name = "UNIT_TYPE")
    private String unitType = "FL";

    @Column(name = "REF_TYPE", nullable = false)
    private String accessorialType;

    /**
     * For read-only purposes. Will be empty for SRA, CRA, etc
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REF_TYPE", insertable = false, updatable = false)
    private AccessorialTypeEntity accessorialDictionary;

    @Column(name = "SHIP_CARR", nullable = false)
    @Enumerated(EnumType.STRING)
    private CostDetailOwner owner;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "COST_DETAIL_ID")
    private LoadCostDetailsEntity costDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FINAN_ADJ_ACC_DETAIL_ID")
    private FinancialAccessorialsEntity financialAccessorials;

    /**
     * Bill To for Adjustments
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BILL_TO_ID")
    private BillToEntity billTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REASON")
    private FinancialReasonsEntity reason;

    @Column(name = "NOTE")
    private String note;

    @Column(name = "CARRIER_ORG_ID")
    private Long carrierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARRIER_ORG_ID", insertable = false, updatable = false)
    private CarrierEntity carrier;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public Long getDedicatedUnitId() {
        return dedicatedUnitId;
    }

    public void setDedicatedUnitId(Long dedicatedUnitId) {
        this.dedicatedUnitId = dedicatedUnitId;
    }

    public String getRateIndicator() {
        return rateIndicator;
    }

    public void setRateIndicator(String rateIndicator) {
        this.rateIndicator = rateIndicator;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getAccessorialType() {
        return accessorialType;
    }

    public void setAccessorialType(String accessorialType) {
        this.accessorialType = accessorialType;
    }

    public AccessorialTypeEntity getAccessorialDictionary() {
        return accessorialDictionary;
    }

    public CostDetailOwner getOwner() {
        return owner;
    }

    public void setOwner(CostDetailOwner owner) {
        this.owner = owner;
    }

    public LoadCostDetailsEntity getCostDetails() {
        return costDetails;
    }

    public void setCostDetails(LoadCostDetailsEntity costDetails) {
        this.costDetails = costDetails;
    }

    public FinancialAccessorialsEntity getFinancialAccessorials() {
        return financialAccessorials;
    }

    public void setFinancialAccessorials(FinancialAccessorialsEntity financialAccessorials) {
        this.financialAccessorials = financialAccessorials;
    }

    public BillToEntity getBillTo() {
        return billTo;
    }

    public void setBillTo(BillToEntity billTo) {
        this.billTo = billTo;
    }

    public FinancialReasonsEntity getReason() {
        return reason;
    }

    public void setReason(FinancialReasonsEntity reason) {
        this.reason = reason;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    public CarrierEntity getCarrier() {
        return carrier;
    }

    public void setCarrier(CarrierEntity carrier) {
        this.carrier = carrier;
    }

    @Override
    public PlainModificationObject  getModification() {
        return modification;
    }

    /**
     * Method makes a copy of {@link CostDetailItemEntity}.
     * @return a copy of {@link CostDetailItemEntity}.
     */
    public CostDetailItemEntity copy() {
        CostDetailItemEntity copy = new CostDetailItemEntity();
        copy.setAccessorialType(getAccessorialType());
        copy.setOwner(getOwner());
        copy.setBillTo(getBillTo());
        copy.setNote(getNote());
        copy.setSubtotal(getSubtotal());
        copy.setUnitCost(getUnitCost());
        copy.setCarrierId(getCarrierId());
        copy.setBillTo(getBillTo());
        return copy;
    }
}

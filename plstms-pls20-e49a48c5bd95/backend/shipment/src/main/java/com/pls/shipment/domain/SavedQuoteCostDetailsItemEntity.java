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

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.organization.CarrierEntity;

/**
 * Saved Quote Cost details Item entity.
 *
 * @author Mikhail Boldinov, 26/03/13
 */
@Entity
@Table(name = "SV_QT_COST_DETAIL_ITEMS")
public class SavedQuoteCostDetailsItemEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 2152366986070420972L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "saved_quote_cost_details_item_sequence")
    @SequenceGenerator(name = "saved_quote_cost_details_item_sequence", sequenceName = "SAVED_QUOTE_COST_DET_ITEMS_SEQ", allocationSize = 1)
    @Column(name = "ITEM_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUOTE_COST_DETAIL_ID", nullable = false)
    private SavedQuoteCostDetailsEntity costDetails;

    @Column(name = "REF_TYPE", nullable = false)
    private String refType;

    @Column(name = "SUBTOTAL")
    private BigDecimal subTotal;

    @Column(name = "QUANTITY")
    private BigDecimal quantity;

    @Column(name = "SHIP_CARR", nullable = false)
    @Enumerated(EnumType.STRING)
    private CostDetailOwner owner;

    @Column(name = "UNIT_TYPE")
    private String unitType;

    @Column(name = "UNIT_COST")
    private BigDecimal unitCost;

    @Column(name = "AMOUNT_UOM")
    private String amountUom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARRIER_ORG_ID")
    private CarrierEntity carrier;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Column(name = "VERSION", nullable = false)
    private long version = 1;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public SavedQuoteCostDetailsEntity getCostDetails() {
        return costDetails;
    }

    public void setCostDetails(SavedQuoteCostDetailsEntity costDetails) {
        this.costDetails = costDetails;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public CostDetailOwner getOwner() {
        return owner;
    }

    public void setOwner(CostDetailOwner owner) {
        this.owner = owner;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public String getAmountUom() {
        return amountUom;
    }

    public void setAmountUom(String amountUom) {
        this.amountUom = amountUom;
    }

    public CarrierEntity getCarrier() {
        return carrier;
    }

    public void setCarrier(CarrierEntity carrier) {
        this.carrier = carrier;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}

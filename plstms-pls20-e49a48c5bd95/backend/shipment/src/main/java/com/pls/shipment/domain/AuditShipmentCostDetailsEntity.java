package com.pls.shipment.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.shared.DisputeCost;
import com.pls.core.shared.UpdateRevenueOption;

/**
 * The carrier invoice additional details entity.
 * 
 * @author Brichak Aleksandr
 * 
 */
@Entity
@Table(name = "AUDIT_SHIPMENT_COST_DETAILS")
public class AuditShipmentCostDetailsEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 811543422073854636L;
    public static final String Q_BY_LOAD_ID = "com.pls.shipment.domain.AuditShipmentCostDetailsEntity.Q_BY_LOAD_ID";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carr_inv_add_det")
    @SequenceGenerator(name = "carr_inv_add_det", sequenceName = "CARR_INV_ADD_DET_SEQ", allocationSize = 1)
    @Column(name = "ADD_DETAIL_ID")
    private Long id;

    @Column(name = "UPDATE_REVENUE")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.shared.UpdateRevenueOption"),
            @Parameter(name = "identifierMethod", value = "getCode"),
            @Parameter(name = "valueOfMethod", value = "getByCode") })
    private UpdateRevenueOption updateRevenue;

    @Column(name = "UPDATE_REVENUE_VALUE")
    private BigDecimal updateRevenueValue;

    @Column(name = "DISPUTE_COST")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.shared.DisputeCost"),
            @Parameter(name = "identifierMethod", value = "getCode"),
            @Parameter(name = "valueOfMethod", value = "getByCode") })
    private DisputeCost disputeCost;

    @Column(name = "REQUEST_PAPERWORK")
    @Type(type = "yes_no")
    private Boolean requestPaperwork;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COST_DETAIL_ID")
    private LoadCostDetailsEntity loadCostDetail;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlainModificationObject getModification() {
        return modification;
    }

    public UpdateRevenueOption getUpdateRevenue() {
        return updateRevenue;
    }

    public DisputeCost getDisputeCost() {
        return disputeCost;
    }

    public void setDisputeCost(DisputeCost disputeCost) {
        this.disputeCost = disputeCost;
    }

    public LoadCostDetailsEntity getLoadCostDetail() {
        return loadCostDetail;
    }

    public void setLoadCostDetail(LoadCostDetailsEntity loadCostDetail) {
        this.loadCostDetail = loadCostDetail;
    }

    public BigDecimal getUpdateRevenueValue() {
        return updateRevenueValue;
    }

    public void setUpdateRevenueValue(BigDecimal updateRevenueValue) {
        this.updateRevenueValue = updateRevenueValue;
    }

    public void setUpdateRevenue(UpdateRevenueOption updateRevenue) {
        this.updateRevenue = updateRevenue;
    }

    public Boolean getRequestPaperwork() {
        return requestPaperwork;
    }

    public void setRequestPaperwork(Boolean requestPaperwork) {
        this.requestPaperwork = requestPaperwork;
    }

    /**
     * Method makes a deep copy of {@link AuditShipmentCostDetailsEntity}.
     * 
     * @return a copy of {@link AuditShipmentCostDetailsEntity}.
     */
    public AuditShipmentCostDetailsEntity copy() {
        AuditShipmentCostDetailsEntity newAuditShipmentCostDetails = new AuditShipmentCostDetailsEntity();
        newAuditShipmentCostDetails.setDisputeCost(getDisputeCost());
        newAuditShipmentCostDetails.setRequestPaperwork(getRequestPaperwork());
        newAuditShipmentCostDetails.setUpdateRevenue(getUpdateRevenue());
        newAuditShipmentCostDetails.setUpdateRevenueValue(getUpdateRevenueValue());
        return newAuditShipmentCostDetails;
    }

}

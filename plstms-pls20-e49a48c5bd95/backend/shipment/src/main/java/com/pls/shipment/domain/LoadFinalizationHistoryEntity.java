package com.pls.shipment.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.domain.Identifiable;
import com.pls.core.domain.enums.ShipmentFinancialStatus;

/**
 * The entity mapped to load_finalization_history.
 * @author Hima Bindu Challa
 *
 */
@Entity
@Table(name = "load_finalization_history")
public class LoadFinalizationHistoryEntity implements Identifiable<Long> {
    private static final long serialVersionUID = -5616888999897701759L;

    public static final String I_LOADS = "com.pls.shipment.domain.LoadFinalizationHistoryEntity.I_LOADS";
    public static final String I_ADJUSTMENTS = "com.pls.shipment.domain.LoadFinalizationHistoryEntity.I_ADJUSTMENTS";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lfh_sequence")
    @SequenceGenerator(name = "lfh_sequence", sequenceName = "LFH_SEQ", allocationSize = 1)
    @Column(name = "LFH_ID")
    private Long id;

    @Column(name = "REQUEST_ID")
    private Long requestId;

    @Column(name = "FINALIZATION_STATUS")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.ShipmentFinancialStatus"),
            @Parameter(name = "identifierMethod", value = "getStatusCode"), @Parameter(name = "valueOfMethod", value = "getByCode") })
    private ShipmentFinancialStatus finalizationStatus;

    @Column(name = "PREV_FIN_STATUS")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.ShipmentFinancialStatus"),
            @Parameter(name = "identifierMethod", value = "getStatusCode"), @Parameter(name = "valueOfMethod", value = "getByCode") })
    private ShipmentFinancialStatus prevFinalizationStatus;

    @Column(name = "LOAD_ID")
    private Long loadId;

    @Column(name = "FAA_DETAIL_ID")
    private Long faaDetailId;

    @Column(name = "DATE_CREATED")
    private Date creationDate;

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public ShipmentFinancialStatus getFinalizationStatus() {
        return finalizationStatus;
    }

    public void setFinalizationStatus(ShipmentFinancialStatus finalizationStatus) {
        this.finalizationStatus = finalizationStatus;
    }

    public ShipmentFinancialStatus getPrevFinalizationStatus() {
        return prevFinalizationStatus;
    }

    public void setPrevFinalizationStatus(ShipmentFinancialStatus prevFinalizationStatus) {
        this.prevFinalizationStatus = prevFinalizationStatus;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public Long getFaaDetailId() {
        return faaDetailId;
    }

    public void setFaaDetailId(Long faaDetailId) {
        this.faaDetailId = faaDetailId;
    }

}

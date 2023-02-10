package com.pls.shipment.domain;

import com.pls.core.domain.Identifiable;
import com.pls.core.domain.enums.ShipmentStatus;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Current entity contains history data for shipments.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Entity
@Table(name = "LOAD_HISTORY")
public class ShipmentHistoryEntity implements Identifiable<Long> {
    private static final long serialVersionUID = -6960793614608676766L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "load_history_sequence")
    @SequenceGenerator(name = "load_history_sequence", sequenceName = "LH_SEQ", allocationSize = 1)
    @Column(name = "LOAD_HISTORY_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAD_ID", nullable = false)
    private LoadEntity shipment;

    @Column(name = "LOAD_STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.LoadStatusUserType")
    private ShipmentStatus shipmentStatus;

    @Column(name = "PREV_LOAD_STATUS")
    @Type(type = "com.pls.core.domain.usertype.LoadStatusUserType")
    private ShipmentStatus previousShipmentStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_CREATED", nullable = false, updatable = false)
    private Date createdDate;

    @Column(name = "CREATED_BY", nullable = false, updatable = false)
    private Long createdBy;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoadEntity getShipment() {
        return shipment;
    }

    public void setShipment(LoadEntity shipment) {
        this.shipment = shipment;
    }

    public ShipmentStatus getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(ShipmentStatus shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    public ShipmentStatus getPreviousShipmentStatus() {
        return previousShipmentStatus;
    }

    public void setPreviousShipmentStatus(ShipmentStatus previousShipmentStatus) {
        this.previousShipmentStatus = previousShipmentStatus;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
}

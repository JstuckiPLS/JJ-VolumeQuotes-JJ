package com.pls.shipment.domain;

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

import org.hibernate.annotations.Type;

import com.pls.core.domain.Identifiable;
import com.pls.core.shared.Status;

/**
 * Carrier Invoice Detail Reason entity.
 *
 * @author Alexander Nalapko
 */
@Entity
@Table(name = "CARRIER_INVOICE_REASON_LINKS")
public class CarrierInvoiceDetailReasonLinksEntity implements Identifiable<Long> {

    private static final long serialVersionUID = 2666115078314939808L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carrier_invoice_reason_links_sequence")
    @SequenceGenerator(name = "carrier_invoice_reason_links_sequence", sequenceName = "INVOICE_REASON_LINKS_SEQ", allocationSize = 1)
    @Column(name = "LINK_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARRIER_INVOICE_ID", nullable = false)
    private CarrierInvoiceDetailsEntity carrierInvoiceDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REASON_ID", nullable = false)
    private CarrierInvoiceDetailReasonsEntity reason;

    @Column(name = "STATUS")
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CarrierInvoiceDetailsEntity getCarrierInvoiceDetails() {
        return carrierInvoiceDetails;
    }

    public void setCarrierInvoiceDetails(CarrierInvoiceDetailsEntity carrierInvoiceDetails) {
        this.carrierInvoiceDetails = carrierInvoiceDetails;
    }

    public CarrierInvoiceDetailReasonsEntity getReason() {
        return reason;
    }

    public void setReason(CarrierInvoiceDetailReasonsEntity reason) {
        this.reason = reason;
    }

}

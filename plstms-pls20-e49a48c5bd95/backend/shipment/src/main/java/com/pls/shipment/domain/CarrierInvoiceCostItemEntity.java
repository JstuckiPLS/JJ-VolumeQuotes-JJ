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
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

/**
 * Entity to save cost details for carrier invoices.
 * 
 * @author Aleksandr Leshchenko
 */
@Entity
@Table(name = "CARRIER_INVOICE_COST_ITEMS")
public class CarrierInvoiceCostItemEntity implements Identifiable<Long>, HasModificationInfo {
    private static final long serialVersionUID = 4082905087452897047L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "car_inv_cost_item_sequence")
    @SequenceGenerator(name = "car_inv_cost_item_sequence", sequenceName = "CARRIER_INVOICE_COST_ITEMS_SEQ", allocationSize = 1)
    @Column(name = "INVOICE_COST_DETAIL_ITEM_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVOICE_DET_ID", nullable = false)
    private CarrierInvoiceDetailsEntity carrierInvoiceDetails;

    @Column(name = "SUBTOTAL", columnDefinition = "NUMBER(10,2)")
    private BigDecimal subtotal;

    @Column(name = "REF_TYPE", nullable = false)
    private String accessorialType;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION")
    private long version = 1L;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public CarrierInvoiceDetailsEntity getCarrierInvoiceDetails() {
        return carrierInvoiceDetails;
    }

    public void setCarrierInvoiceDetails(CarrierInvoiceDetailsEntity carrierInvoiceDetails) {
        this.carrierInvoiceDetails = carrierInvoiceDetails;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getAccessorialType() {
        return accessorialType;
    }

    public void setAccessorialType(String accessorialType) {
        this.accessorialType = accessorialType;
    }

    @Override
    public PlainModificationObject  getModification() {
        return modification;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}

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

import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.shared.Status;

/**
 * Carrier Invoice line item entity.
 *
 * @author Mikhail Boldinov, 28/08/13
 */
@Entity
@Table(name = "CARRIER_INVOICE_LINE_ITEMS")
public class CarrierInvoiceLineItemEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -513271123447182678L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carrier_invoice_line_items_sequence")
    @SequenceGenerator(name = "carrier_invoice_line_items_sequence", sequenceName = "CARRIER_INVOICE_LINE_ITEMS_SEQ", allocationSize = 1)
    @Column(name = "INVOICE_LINE_ITEM_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVOICE_DET_ID", nullable = false)
    private CarrierInvoiceDetailsEntity carrierInvoiceDetails;

    @Column(name = "ORDER_NUM")
    private Integer orderNumber;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "WEIGHT")
    private BigDecimal weight;

    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name = "PACKAGING_CODE")
    private String packagingCode;

    @Column(name = "COMMODITY_CODE")
    private String nmfc;

    @Column(name = "COMMODITY_CLASS_CODE", nullable = true)
    @Type(type = "com.pls.core.domain.usertype.CommodityClassUserType")
    private CommodityClass commodityClass;

    @Column(name = "CHARGE")
    private BigDecimal charge;

    @Column(name = "SPECIAL_CHARGE_CODE")
    private String specialChargeCode;

    @Column(name = "STATUS")
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status;

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

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getPackagingCode() {
        return packagingCode;
    }

    public void setPackagingCode(String packagingCode) {
        this.packagingCode = packagingCode;
    }

    public String getNmfc() {
        return nmfc;
    }

    public void setNmfc(String nmfc) {
        this.nmfc = nmfc;
    }

    public CommodityClass getCommodityClass() {
        return commodityClass;
    }

    public void setCommodityClass(CommodityClass commodityClass) {
        this.commodityClass = commodityClass;
    }

    public BigDecimal getCharge() {
        return charge;
    }

    public void setCharge(BigDecimal charge) {
        this.charge = charge;
    }

    public String getSpecialChargeCode() {
        return specialChargeCode;
    }

    public void setSpecialChargeCode(String specialChargeCode) {
        this.specialChargeCode = specialChargeCode;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

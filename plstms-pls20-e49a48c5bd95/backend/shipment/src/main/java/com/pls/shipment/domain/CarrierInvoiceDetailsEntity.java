package com.pls.shipment.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.OrganizationEntity;
import com.pls.core.shared.Status;

/**
 * Carrier Invoice Details entity.
 *
 * @author Mikhail Boldinov, 28/08/13
 */
@Entity
@Table(name = "CARRIER_INVOICE_DETAILS")
public class CarrierInvoiceDetailsEntity implements Identifiable<Long>, HasModificationInfo {
    private static final long serialVersionUID = 5265501590467027267L;

    public static final String Q_ARCHIVE = "com.pls.shipment.domain.CarrierInvoiceDetailsEntity.Q_ARCHIVE";
    public static final String Q_ARCHIVE_LIST = "com.pls.shipment.domain.CarrierInvoiceDetailsEntity.Q_ARCHIVE_LIST";
    public static final String Q_GET_UNMATCHED = "com.pls.shipment.domain.CarrierInvoiceDetailsEntity.Q_GET_UNMATCHED";
    public static final String Q_GET_ARCHIVED = "com.pls.shipment.domain.CarrierInvoiceDetailsEntity.Q_GET_ARCHIVED";
    public static final String Q_ARCHIVE_UNMATCHED = "com.pls.shipment.domain.CarrierInvoiceDetailsEntity.Q_ARCHIVE_UNMATCHED";
    public static final String Q_GET_EDI_FOR_LOAD = "com.pls.shipment.domain.CarrierInvoiceDetailsEntity.Q_GET_EDI_FOR_LOAD";
    public static final String Q_UPDATE_STATUS_AND_MATCHED_LOAD =
            "com.pls.shipment.domain.CarrierInvoiceDetailsEntity.Q_UPDATE_STATUS_AND_MATCHED_LOAD";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carrier_invoice_details_sequence")
    @SequenceGenerator(name = "carrier_invoice_details_sequence", sequenceName = "CARRIER_INVOICE_DETAILS_SEQ", allocationSize = 1)
    @Column(name = "INVOICE_DET_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARRIER_ID", nullable = false)
    private CarrierEntity carrier;

    @Column(name = "INVOICE_NUM")
    private String invoiceNumber;

    @Column(name = "INVOICE_DATE")
    private Date invoiceDate;

    @Column(name = "REFERENCE_NUM")
    private String referenceNumber;

    @Column(name = "PAY_TERMS")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.PaymentTerms"),
            @Parameter(name = "identifierMethod", value = "getPaymentTermsCode"),
            @Parameter(name = "valueOfMethod", value = "getByCode")})
    private PaymentTerms paymentTerms;

    @Column(name = "NET_AMOUNT")
    private BigDecimal netAmount;

    @Column(name = "DELIVERY_DATE")
    private Date deliveryDate;

    @Column(name = "EST_DELIVERY_DATE")
    private Date estDeliveryDate;

    @Column(name = "BOL")
    private String bolNumber;

    @Column(name = "PO_NUM")
    private String poNumber;

    @Column(name = "SHIPPER_REFERENCE_NUMBER")
    private String shipperRefNumber;

    @Column(name = "PRO_NUMBER")
    private String proNumber;

    @Column(name = "EDI_ACCOUNT")
    private String ediAccount;

    @Column(name = "ACT_PICKUP_DATE")
    private Date actualPickupDate;

    @Column(name = "TOTAL_WEIGHT")
    private BigDecimal totalWeight;

    @Column(name = "TOTAL_CHARGES")
    private BigDecimal totalCharges;

    @Column(name = "TOTAL_QUANTITY")
    private Integer totalQuantity;

    /**
     * <code>true</code> if vendor bill has been created via EDI.
     */
    @Column(name = "EDI")
    @Type(type = "yes_no")
    private Boolean edi = false;

    @Column(name = "MATCHED_LOAD_ID")
    private Long matchedLoadId;

    @Column(name = "STATUS")
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status;

    @OneToMany(mappedBy = "carrierInvoiceDetails", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CarrierInvoiceLineItemEntity> carrierInvoiceLineItems;

    @OneToMany(mappedBy = "carrierInvoiceDetails", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CarrierInvoiceCostItemEntity> carrierInvoiceCostItems;

    @OneToOne(optional = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "INVOICE_DET_ID", referencedColumnName = "INVOICE_DET_ID",
                    unique = true, nullable = true, insertable = false, updatable = false)),
            @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "ADDRESS_TYPE", value = "'SH'")) })
    private CarrierInvoiceAddressDetailsEntity originAddress;

    @OneToOne(optional = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "INVOICE_DET_ID", referencedColumnName = "INVOICE_DET_ID",
                    unique = true, nullable = true, insertable = false, updatable = false)),
            @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "ADDRESS_TYPE", value = "'CN'")) })
    private CarrierInvoiceAddressDetailsEntity destinationAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EDI_ACCOUNT", referencedColumnName = "EDI_ACCOUNT", insertable = false, updatable = false)
    private OrganizationEntity customer;

    @OneToOne(optional = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = "INVOICE_DET_ID", referencedColumnName = "CARRIER_INVOICE_ID",
                unique = false, nullable = true, insertable = false, updatable = false)),
        @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "STATUS", value = "'A'")) })
    private CarrierInvoiceDetailReasonLinksEntity reasonLink;

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

    public CarrierInvoiceDetailReasonLinksEntity getReasonLink() {
        return reasonLink;
    }

    public void setReasonLinks(CarrierInvoiceDetailReasonLinksEntity reasonLink) {
        this.reasonLink = reasonLink;
    }

    public CarrierEntity getCarrier() {
        return carrier;
    }

    public void setCarrier(CarrierEntity carrier) {
        this.carrier = carrier;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public PaymentTerms getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(PaymentTerms paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Date getEstDeliveryDate() {
        return estDeliveryDate;
    }

    public void setEstDeliveryDate(Date estDeliveryDate) {
        this.estDeliveryDate = estDeliveryDate;
    }

    public String getBolNumber() {
        return bolNumber;
    }

    public void setBolNumber(String bolNumber) {
        this.bolNumber = bolNumber;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getShipperRefNumber() {
        return shipperRefNumber;
    }

    public void setShipperRefNumber(String shipperRefNumber) {
        this.shipperRefNumber = shipperRefNumber;
    }

    public String getProNumber() {
        return proNumber;
    }

    public void setProNumber(String proNumber) {
        this.proNumber = proNumber;
    }

    public String getEdiAccount() {
        return ediAccount;
    }

    public void setEdiAccount(String ediAccount) {
        this.ediAccount = ediAccount;
    }

    public Date getActualPickupDate() {
        return actualPickupDate;
    }

    public void setActualPickupDate(Date actualPickupDate) {
        this.actualPickupDate = actualPickupDate;
    }

    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    public BigDecimal getTotalCharges() {
        return totalCharges;
    }

    public void setTotalCharges(BigDecimal totalCharges) {
        this.totalCharges = totalCharges;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Boolean getEdi() {
        return edi;
    }

    public void setEdi(Boolean edi) {
        this.edi = edi;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<CarrierInvoiceLineItemEntity> getCarrierInvoiceLineItems() {
        return carrierInvoiceLineItems;
    }

    /**
     * Set line item to carrier invoice details entity.
     *
     * @param carrierInvoiceLineItems - set of {@link CarrierInvoiceLineItemEntity}
     */
    public void setCarrierInvoiceLineItems(Set<CarrierInvoiceLineItemEntity> carrierInvoiceLineItems) {
        if (this.carrierInvoiceLineItems == null) {
            this.carrierInvoiceLineItems = new HashSet<CarrierInvoiceLineItemEntity>();
        } else {
            this.carrierInvoiceLineItems.clear();
        }
        this.carrierInvoiceLineItems.addAll(carrierInvoiceLineItems);
        if (!this.carrierInvoiceLineItems.isEmpty()) {
            for (CarrierInvoiceLineItemEntity lineItemEntity : this.carrierInvoiceLineItems) {
                lineItemEntity.setCarrierInvoiceDetails(this);
            }
        }
    }

    public Set<CarrierInvoiceCostItemEntity> getCarrierInvoiceCostItems() {
        return carrierInvoiceCostItems;
    }

    /**
     * Set cost item to carrier invoice details entity.
     *
     * @param carrierInvoiceCostItems - set of {@link CarrierInvoiceCostItemEntity}
     */
    public void setCarrierInvoiceCostItems(Set<CarrierInvoiceCostItemEntity> carrierInvoiceCostItems) {
        if (this.carrierInvoiceCostItems == null) {
            this.carrierInvoiceCostItems = new HashSet<CarrierInvoiceCostItemEntity>();
        } else {
            this.carrierInvoiceCostItems.clear();
        }
        this.carrierInvoiceCostItems.addAll(carrierInvoiceCostItems);
        if (!this.carrierInvoiceCostItems.isEmpty()) {
            for (CarrierInvoiceCostItemEntity costItemEntity : this.carrierInvoiceCostItems) {
                costItemEntity.setCarrierInvoiceDetails(this);
            }
        }
    }

    public CarrierInvoiceAddressDetailsEntity getOriginAddress() {
        return originAddress;
    }

    /**
     * Set origin address to carrier invoice details entity.
     *
     * @param originAddress - origin address
     */
    public void setOriginAddress(CarrierInvoiceAddressDetailsEntity originAddress) {
        this.originAddress = originAddress;
        if (originAddress != null) {
            this.originAddress.setCarrierInvoiceDetails(this);
        }
    }

    public CarrierInvoiceAddressDetailsEntity getDestinationAddress() {
        return destinationAddress;
    }

    /**
     * Set destination address to carrier invoice details entity.
     *
     * @param destinationAddress - destination address
     */
    public void setDestinationAddress(CarrierInvoiceAddressDetailsEntity destinationAddress) {
        this.destinationAddress = destinationAddress;
        if (destinationAddress != null) {
            this.destinationAddress.setCarrierInvoiceDetails(this);
        }
    }

    public OrganizationEntity getCustomer() {
        return customer;
    }

    public void setCustomer(OrganizationEntity customer) {
        this.customer = customer;
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

    public Long getMatchedLoadId() {
        return matchedLoadId;
    }

    public void setMatchedLoadId(Long matchedLoadId) {
        this.matchedLoadId = matchedLoadId;
    }
}

package com.pls.shipment.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.domain.Identifiable;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.user.CustomerUserEntity;

/**
 * Mapping to the PLS_BILLING_INVOICE_AUDIT_VIEW view.
 * 
 * @author Dmitry Nikolaenko
 *
 */
@Entity
@Table(name = "PLS_BILLING_INVOICE_AUDIT_VIEW")
@Immutable
public class InvoiceAuditViewEntity implements Identifiable<Long> {

    public static final String Q_GET_LOADS_FOR_BILLING_INVOICE = "com.pls.shipment.domain.InvoiceAuditViewEntity.Q_GET_LOADS_FOR_BILLING_INVOICE";

    private static final long serialVersionUID = 8913655564227314351L;

    @Column(name = "ORG_ID", insertable = false, updatable = false, nullable = false)
    private Long carrierId;

    @Column(name = "LOCATION_ID")
    private Long locationId;

    @Id
    @Column(name = "LOAD_ID")
    private Long id;

    @Column(name = "FAA_DETAIL_ID")
    private Long adjustmentId;

    @Column(name = "BOL")
    private String bolNumber;

    @Column(name = "PO_NUM")
    private String poNumber;

    @Column(name = "PRO_NUM")
    private String proNumber;

    @Column(name = "REVENUE")
    private BigDecimal revenue;

    @Column(name = "COSTS")
    private BigDecimal cost;

    @Column(name = "MARGIN")
    private BigDecimal margin;

    @Column(name = "FRT_BILL_AMOUNT")
    private BigDecimal vendorBillAmount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DLV_DATE")
    private Date departure;

    @Column(name = "RSN_DESCR")
    private String reason;

    @Column(name = "CARRIER")
    private String carrierName;

    @Column(name = "SHIPPER")
    private String customerName;

    @Column(name = "NETW")
    private String networkName;

    @Column(name = "USERNAME")
    private String accountExecutiveName;

    @Column(name = "NOTE")
    private String noteComment;

    @Column(name = "NOTE_DT_CREATED")
    private Date noteDateCreated;

    @Column(name = "CREATED_USERNAME")
    private String createdUsername;

    @Column(name = "NOTE_CNT")
    private Long numberOfnotes;

    @Column(name = "DIFF_FROM_CURRENT_DATE")
    private Long diffDays;

    @Column(name = "DATE_MODIFIED")
    private Date modifiedDate;

    @Column(name = "SCAC")
    private String scac;

    @Column(name = "PRICE_AUDIT_DATE")
    private Date priceAuditDate;

    @Column(name = "invoiceType")
    @Enumerated(EnumType.STRING)
    private InvoiceType invoiceType;

    @Column(name = "ISREBILL", nullable = true)
    @Type(type = "true_false")
    private Boolean rebill;

    @Column(name = "FINALIZATION_STATUS")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.ShipmentFinancialStatus"),
            @Parameter(name = "identifierMethod", value = "getStatusCode"),
            @Parameter(name = "valueOfMethod", value = "getByCode") })
    private ShipmentFinancialStatus finalizationStatus = ShipmentFinancialStatus.NONE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORG_ID", nullable = false)
    private CustomerEntity organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAD_ID", nullable = false, insertable = false, updatable = false)
    private LoadEntity load;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FAA_DETAIL_ID", insertable = false, updatable = false)
    private FinancialAccessorialsEntity financialAccessorialEntity;

    @OneToMany
    @JoinColumns({
            @JoinColumn(name = "ORG_ID", referencedColumnName = "ORG_ID"),
            @JoinColumn(name = "LOCATION_ID", referencedColumnName = "LOCATION_ID")
    })
    private Set<CustomerUserEntity> customerLocationUsers;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getAdjustmentId() {
        return adjustmentId;
    }

    public void setAdjustmentId(Long adjustmentId) {
        this.adjustmentId = adjustmentId;
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

    public String getProNumber() {
        return proNumber;
    }

    public void setProNumber(String proNumber) {
        this.proNumber = proNumber;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getMargin() {
        return margin;
    }

    public void setMargin(BigDecimal margin) {
        this.margin = margin;
    }

    public BigDecimal getVendorBillAmount() {
        return vendorBillAmount;
    }

    public void setVendorBillAmount(BigDecimal vendorBillAmount) {
        this.vendorBillAmount = vendorBillAmount;
    }

    public Date getDeparture() {
        return departure;
    }

    public void setDeparture(Date departure) {
        this.departure = departure;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getAccountExecutiveName() {
        return accountExecutiveName;
    }

    public void setAccountExecutiveName(String accountExecutiveName) {
        this.accountExecutiveName = accountExecutiveName;
    }

    public String getNoteComment() {
        return noteComment;
    }

    public void setNoteComment(String noteComment) {
        this.noteComment = noteComment;
    }

    public Date getNoteDateCreated() {
        return noteDateCreated;
    }

    public void setNoteDateCreated(Date noteDateCreated) {
        this.noteDateCreated = noteDateCreated;
    }

    public String getCreatedUsername() {
        return createdUsername;
    }

    public void setCreatedUsername(String createdUsername) {
        this.createdUsername = createdUsername;
    }

    public Long getNumberOfnotes() {
        return numberOfnotes;
    }

    public void setNumberOfnotes(Long numberOfnotes) {
        this.numberOfnotes = numberOfnotes;
    }

    public Long getDiffDays() {
        return diffDays;
    }

    public void setDiffDays(Long diffDays) {
        this.diffDays = diffDays;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public Date getPriceAuditDate() {
        return priceAuditDate;
    }

    public void setPriceAuditDate(Date priceAuditDate) {
        this.priceAuditDate = priceAuditDate;
    }


    public ShipmentFinancialStatus getFinalizationStatus() {
        return finalizationStatus;
    }

    public void setFinalizationStatus(ShipmentFinancialStatus finalizationStatus) {
        this.finalizationStatus = finalizationStatus;
    }

    public CustomerEntity getOrganization() {
        return organization;
    }

    public void setOrganization(CustomerEntity organization) {
        this.organization = organization;
    }

    public FinancialAccessorialsEntity getFinancialAccessorialEntity() {
        return financialAccessorialEntity;
    }

    public void setFinancialAccessorialEntity(FinancialAccessorialsEntity financialAccessorialEntity) {
        this.financialAccessorialEntity = financialAccessorialEntity;
    }

    public Set<CustomerUserEntity> getCustomerLocationUsers() {
        return customerLocationUsers;
    }

    public void setCustomerLocationUsers(Set<CustomerUserEntity> customerLocationUsers) {
        this.customerLocationUsers = customerLocationUsers;
    }

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public Boolean isRebill() {
        return rebill;
    }

    public void setRebill(Boolean rebill) {
        this.rebill = rebill;
    }

    public LoadEntity getLoad() {
        return load;
    }

    public void setLoad(LoadEntity load) {
        this.load = load;
    }
}

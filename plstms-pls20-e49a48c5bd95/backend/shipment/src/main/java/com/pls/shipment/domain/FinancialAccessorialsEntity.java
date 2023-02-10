package com.pls.shipment.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.shared.Status;
import com.pls.core.shared.StatusYesNo;

/**
 * Same as {@link LoadCostDetailsEntity} but for adjustments.
 * 
 * @author Viacheslav Krot
 * @author Aleksandr Leshchenko
 */
@Entity
@Table(name = "FINAN_ADJ_ACC_DETAIL")
public class FinancialAccessorialsEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 5381744017437445260L;

    public static final String Q_UPDATE_FIN_ADJ_INVOICE_APPROVED =
            "com.pls.shipment.domain.FinancialAccessorialsEntity.Q_UPDATE_FIN_ADJ_INVOICE_APPROVED";
    public static final String Q_UPDATE_FIN_ADJ_FINANCIAL_STATUS =
            "com.pls.shipment.domain.FinancialAccessorialsEntity.Q_UPDATE_FIN_ADJ_FINANCIAL_STATUS";
    public static final String Q_SET_INVOICE_NUMBER = "com.pls.shipment.domain.FinancialAccessorialsEntity.Q_SET_INVOICE_NUMBER";
    public static final String Q_GET_ADJ_FOR_TRANSACTIONAL_INVOICES =
            "com.pls.shipment.domain.FinancialAccessorialsEntity.Q_GET_ADJ_FOR_TRANSACTIONAL_INVOICES";
    public static final String Q_GET_ADJ_FOR_CONSOLIDATED_INVOICES =
            "com.pls.shipment.domain.FinancialAccessorialsEntity.Q_GET_ADJ_FOR_CONSOLIDATED_INVOICES";
    public static final String Q_GET_COST_ITEMS_COUNT = "com.pls.shipment.domain.FinancialAccessorialsEntity.Q_GET_COST_ITEMS_COUNT";
    public static final String Q_GET_OTHER_REBILL_ADJUSTMENTS = "com.pls.shipment.domain.FinancialAccessorialsEntity.Q_GET_OTHER_REBILL_ADJUSTMENTS";
    public static final String Q_GET_REBILL_ADJ_FOR_AUTO_PROCESSING =
            "com.pls.shipment.domain.FinancialAccessorialsEntity.Q_GET_REBILL_ADJ_FOR_AUTO_PROCESSING";
    public static final String Q_GET_CBI_DATA_ADJ = "com.pls.shipment.domain.FinancialAccessorialsEntity.Q_GET_CBI_DATA_ADJ";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "financial_accessorial_details_sequence")
    @SequenceGenerator(name = "financial_accessorial_details_sequence", sequenceName = "FINAN_ADJ_ACC_DETAIL_SEQ", allocationSize = 1)
    @Column(name = "FAA_DETAIL_ID")
    private Long id;

    /**
     * Revision field should be unique for load among active adjustments.
     */
    @Column(name = "REVISION")
    private Integer revision;

    /**
     * Hardcoded value for adjustments.
     */
    @Column(name = "ADJ_ACC")
    private String adjustmentAccessorial = "ADJ";

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAD_ID")
    private LoadEntity load;

    @OneToMany(mappedBy = "financialAccessorials", fetch = FetchType.LAZY)
    private Set<FinancialAccountReceivablesEntity> accountReceivables;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @Column(name = "SENT_TO_FINANCE", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.YesNoUserType")
    private StatusYesNo sentToFinance = StatusYesNo.NO;

    @Column(name = "INVOICED_IN_FINAN")
    @Type(type = "com.pls.core.domain.usertype.YesNoUserType")
    private StatusYesNo invoicedInFinance;

    @Column(name = "SHORT_PAY")
    @Type(type = "yes_no")
    private Boolean shortPay = false;

    @Column(name = "GL_DATE")
    private Date generalLedgerDate;

    @Column(name = "FAA_STATUS")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.ShipmentFinancialStatus"),
            @Parameter(name = "identifierMethod", value = "getStatusCode"), @Parameter(name = "valueOfMethod", value = "getByCode") })
    private ShipmentFinancialStatus financialStatus;

    @Column(name = "INV_APPROVED")
    @Type(type = "yes_no")
    private Boolean invoiceApproved = true;

    @Column(name = "TOTAL_REVENUE", nullable = false)
    private BigDecimal totalRevenue;

    @Column(name = "TOTAL_COSTS", nullable = false)
    private BigDecimal totalCost;

    @OneToMany(mappedBy = "financialAccessorials", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<CostDetailItemEntity> costDetailItems = new LinkedHashSet<CostDetailItemEntity>();

    @OneToMany(mappedBy = "financialAccessorials", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<FinancialAccessorialsAdditionalInfoEntity> rollbackInfo = new HashSet<FinancialAccessorialsAdditionalInfoEntity>();

    @OneToMany(mappedBy = "financialAccessorials", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<FinancialAccessorialsProductInfoEntity> adjProductInfo = new HashSet<FinancialAccessorialsProductInfoEntity>();

    @Column(name = "CUSTOMER_INVOICE_NUM")
    private String invoiceNumber;

    @Column(name = "GROUP_INVOICE_NUM")
    private String groupInvoiceNumber;

    @Formula("round(((coalesce(total_revenue,0) - coalesce(total_costs,0))*100/"
            + "(case when total_revenue = 0 then 1 else coalesce(total_revenue,1) end))::numeric,2)")
    private BigDecimal margin;

    @Formula("(coalesce(total_revenue,0) - coalesce(total_costs,0))")
    private BigDecimal marginAmt;

    @Column(name = "BOL", nullable = true, updatable = false)
    private String bol;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    @OneToMany(mappedBy = "financialAccessorialEntity", fetch = FetchType.LAZY)
    @Where(clause = "status = 'A'")
    private Set<LdBillingAuditReasonsEntity> billingAuditReasons;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public String getAdjustmentAccessorial() {
        return adjustmentAccessorial;
    }

    public void setAdjustmentAccessorial(String adjustmentAccessorial) {
        this.adjustmentAccessorial = adjustmentAccessorial;
    }

    public LoadEntity getLoad() {
        return load;
    }

    public void setLoad(LoadEntity load) {
        this.load = load;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public StatusYesNo getSentToFinance() {
        return sentToFinance;
    }

    public void setSentToFinance(StatusYesNo sentToFinance) {
        this.sentToFinance = sentToFinance;
    }

    public StatusYesNo getInvoicedInFinance() {
        return invoicedInFinance;
    }

    public void setInvoicedInFinance(StatusYesNo invoicedInFinance) {
        this.invoicedInFinance = invoicedInFinance;
    }

    public Boolean getShortPay() {
        return shortPay;
    }

    public void setShortPay(Boolean shortPay) {
        this.shortPay = shortPay;
    }

    /**
     * Get date when load gets to general ledger.
     * 
     * @return date when load gets to general ledger.
     */
    public Date getGeneralLedgerDate() {
        return generalLedgerDate;
    }

    /**
     * Set date when load gets to general ledger.
     * 
     * @param generalLedgerDate
     *            date when load gets to general ledger.
     */
    public void setGeneralLedgerDate(Date generalLedgerDate) {
        this.generalLedgerDate = generalLedgerDate;
    }

    public ShipmentFinancialStatus getFinancialStatus() {
        return financialStatus;
    }

    public void setFinancialStatus(ShipmentFinancialStatus financialStatus) {
        this.financialStatus = financialStatus;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public Set<CostDetailItemEntity> getCostDetailItems() {
        return costDetailItems;
    }

    public void setCostDetailItems(Set<CostDetailItemEntity> costDetailItems) {
        this.costDetailItems = costDetailItems;
    }

    public Set<FinancialAccessorialsAdditionalInfoEntity> getRollbackInfo() {
        return rollbackInfo;
    }

    public void setRollbackInfo(Set<FinancialAccessorialsAdditionalInfoEntity> rollbackInfo) {
        this.rollbackInfo = rollbackInfo;
    }

    public Set<FinancialAccessorialsProductInfoEntity> getAdjProductInfo() {
        return adjProductInfo;
    }

    public void setAdjProductInfo(Set<FinancialAccessorialsProductInfoEntity> adjProductInfo) {
        this.adjProductInfo = adjProductInfo;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getGroupInvoiceNumber() {
        return groupInvoiceNumber;
    }

    public void setGroupInvoiceNumber(String groupInvoiceNumber) {
        this.groupInvoiceNumber = groupInvoiceNumber;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getInvoiceApproved() {
        return invoiceApproved != null && invoiceApproved;
    }

    public void setInvoiceApproved(Boolean invoiceApproved) {
        this.invoiceApproved = invoiceApproved;
    }

    public BigDecimal getMargin() {
        return margin;
    }

    public void setMargin(BigDecimal margin) {
        this.margin = margin;
    }

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public BigDecimal getMarginAmt() {
        return marginAmt;
    }

    public Set<LdBillingAuditReasonsEntity> getBillingAuditReasons() {
        return billingAuditReasons;
    }

    public void setBillingAuditReasons(Set<LdBillingAuditReasonsEntity> billingAuditReasons) {
        this.billingAuditReasons = billingAuditReasons;
    }

    public Set<FinancialAccountReceivablesEntity> getAccountReceivables() {
        return accountReceivables;
    }

    public void setAccountReceivables(Set<FinancialAccountReceivablesEntity> accountReceivables) {
        this.accountReceivables = accountReceivables;
    }
}

package com.pls.shipment.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.HasVersion;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.shared.Status;
import com.pls.core.shared.StatusYesNo;

/**
 * LoadCost details.
 *
 * Load may have multiple cost details, but only one is active and should be used, all others are history. If you need to get estimated
 * cost - sort them by date and get first one.
 * <p/>
 * SHIP_DATE - estimated pickup date while load is not picked up, and actual when it is picked up.
 * <p/>
 * General ledger date is set in Oracle Financials and gets back to the database automatically.
 *
 * @author Gleb Zgonikov
 * @author Viacheslav Krot
 */
@Entity
@Table(name = "RATER.LOAD_COST_DETAILS")
public class LoadCostDetailsEntity implements Identifiable<Long>, HasModificationInfo, HasVersion {

    public static final String Q_SET_INVOICE_NUMBER = "com.pls.shipment.domain.LoadCostDetailsEntity.Q_SET_INVOICE_NUMBER";
    public static final String Q_GET_COST_ITEMS_COUNT = "com.pls.shipment.domain.LoadCostDetailsEntity.Q_GET_COST_ITEMS_COUNT";

    private static final long serialVersionUID = -8998607943292989405L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cost_details_item_sequence")
    @SequenceGenerator(name = "cost_details_item_sequence", sequenceName = "RATER.LCL_SEQ", allocationSize = 1)
    @Column(name = "COST_DETAIL_ID")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAD_ID")
    private LoadEntity load;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.INACTIVE;

    @OneToMany(mappedBy = "costDetails", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Where(clause = "FINAN_ADJ_ACC_DETAIL_ID is null")
    private Set<CostDetailItemEntity> costDetailItems = new HashSet<CostDetailItemEntity>();

    @OneToMany(mappedBy = "costDetails", fetch = FetchType.LAZY)
    private Set<CostDetailItemEntity> allCostDetailItems = new HashSet<CostDetailItemEntity>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "loadCostDetail")
    private AuditShipmentCostDetailsEntity auditShipmentCostDetails;

    /**
     * Not nullable field.
     */
    @Column(name = "STOPOFFS")
    private Integer stopOffs = 0;

    @Column(name = "TOTAL_REVENUE")
    private BigDecimal totalRevenue;

    @Column(name = "TOTAL_COSTS")
    private BigDecimal totalCost;

    @Formula("round(((coalesce(total_revenue,0) - coalesce(total_costs,0))*100/"
            + "(case when total_revenue = 0 then 1 else coalesce(total_revenue,1) end))::numeric,2)")
    private BigDecimal margin;

    @Formula("(coalesce(total_revenue,0) - coalesce(total_costs,0))")
    private BigDecimal marginAmt;

    /**
     * It is estimated ship date if the load is not yet shipped or actual ship date if the load is already shipped
     */
    @Column(name = "SHIP_DATE")
    private Date shipDate;

    @Column(name = "SENT_TO_FINANCE")
    @Type(type = "com.pls.core.domain.usertype.YesNoUserType")
    private StatusYesNo sentToFinance = StatusYesNo.NO;

    @Column(name = "INVOICED_IN_FINAN")
    @Type(type = "com.pls.core.domain.usertype.YesNoUserType")
    private StatusYesNo invoicedInFinance;

    @Column(name = "GL_DATE")
    private Date generalLedgerDate;

    /**
     * Service type (DIRECT/INDIRECT)
     */
    @Column(name = "SERVICE_TYPE")
    @Enumerated(EnumType.STRING)
    private LtlServiceType serviceType;

    @Column(name = "NEW_PROD_LIAB_AMT")
    private BigDecimal newLiability;

    @Column(name = "USED_PROD_LIAB_AMT")
    private BigDecimal usedLiability;

    @Column(name = "PROHIBITED_COMMODITIES")
    private String prohibitedCommodities;

    @Column(name = "CUSTOMER_INVOICE_NUM")
    private String invoiceNumber;

    @Column(name = "GROUP_INVOICE_NUM")
    private String groupInvoiceNumber;

    @Column(name = "GUARAN_TIME")
    private Long guaranteedBy;

    @Column(name = "GUARAN_BOL_NAME")
    private String guaranteedNameForBOL;

    @Column(name = "PRIC_PROF_DETAIL_ID")
    private Long pricingProfileDetailId;

    @Column(name = "REVENUE_OVERRIDE")
    @Type(type = "com.pls.core.domain.usertype.YesNoUserType")
    private StatusYesNo revenueOverride = StatusYesNo.NO;

    @Column(name = "COST_OVERRIDE")
    @Type(type = "com.pls.core.domain.usertype.YesNoUserType")
    private StatusYesNo costOverride = StatusYesNo.NO;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoadEntity getLoad() {
        return load;
    }

    public void setLoad(LoadEntity load) {
        this.load = load;
    }

    public Set<CostDetailItemEntity> getCostDetailItems() {
        return costDetailItems;
    }

    public void setCostDetailItems(Set<CostDetailItemEntity> costDetailItems) {
        this.costDetailItems = costDetailItems;
    }

    public Set<CostDetailItemEntity> getAllCostDetailItems() {
        return allCostDetailItems;
    }

    public void setAllCostDetailItems(Set<CostDetailItemEntity> allCostDetailItems) {
        this.allCostDetailItems = allCostDetailItems;
    }

    public Integer getStopOffs() {
        return stopOffs;
    }

    public void setStopOffs(Integer stopOffs) {
        this.stopOffs = stopOffs;
    }

    /**
     * Get total revenue, e.g. total amount of money charged to the customer.
     * @return total revenue, e.g. total amount of money charged to the customer.
     */
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    /**
     * Set total revenue, e.g. total amount of money charged to the customer.
     * @param totalRevenue total revenue.
     */
    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    /**
     * Get total cost paid to carrier and for accessorials.
     * @return total cost paid to carrier and for accessorials.
     */
    public BigDecimal getTotalCost() {
        return totalCost;
    }

    /**
     * Set total cost paid to carrier and for accessorials.
     * @param totalCost total cost paid to carrier and for accessorials
     */
    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getMargin() {
        return margin;
    }

    /**
     * Get shipping date. It is estimated ship date if the load is not yet shipped or actual ship date if the load is already shipped
     * @return shipping date.
     * <b >Note:</b> Hima is not sure if this field is in sync for PLS 2.0. Better use {@link LoadDetailsEntity#getScheduledArrival()}.
     */
    public Date getShipDate() {
        return shipDate;
    }

    /**
     * Set shipping date. It is estimated ship date if the load is not yet shipped or actual ship date if the load is already shipped
     * @param shipDate shipping date. See full comment.
     */
    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
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

    /**
     * Get date when load gets to general ledger.
     * @return date when load gets to general ledger.
     */
    public Date getGeneralLedgerDate() {
        return generalLedgerDate;
    }

    public void setGeneralLedgerDate(Date generalLedgerDate) {
        this.generalLedgerDate = generalLedgerDate;
    }

    /**
     * Get service type (DIRECT/INDIRECT).
     *
     * @return service type (DIRECT or INDIRECT)
     */
    public LtlServiceType getServiceType() {
        return serviceType;
    }

    /**
     * Set service type (DIRECT/INDIRECT)..
     *
     * @param serviceType
     *            DIRECT or INDIRECT
     */
    public void setServiceType(LtlServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public BigDecimal getNewLiability() {
        return newLiability;
    }

    public void setNewLiability(BigDecimal newLiability) {
        this.newLiability = newLiability;
    }

    public BigDecimal getUsedLiability() {
        return usedLiability;
    }

    public void setUsedLiability(BigDecimal usedLiability) {
        this.usedLiability = usedLiability;
    }

    public String getProhibitedCommodities() {
        return prohibitedCommodities;
    }

    public void setProhibitedCommodities(String prohibitedCommodities) {
        this.prohibitedCommodities = prohibitedCommodities;
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

    public Long getGuaranteedBy() {
        return guaranteedBy;
    }

    public void setGuaranteedBy(Long guaranteedBy) {
        this.guaranteedBy = guaranteedBy;
    }

    public String getGuaranteedNameForBOL() {
        return guaranteedNameForBOL;
    }

    public void setGuaranteedNameForBOL(String guaranteedNameForBOL) {
        this.guaranteedNameForBOL = guaranteedNameForBOL;
    }

    public Long getPricingProfileDetailId() {
        return pricingProfileDetailId;
    }

    public void setPricingProfileDetailId(Long pricingProfileDetailId) {
        this.pricingProfileDetailId = pricingProfileDetailId;
    }

    public BigDecimal getMarginAmt() {
        return marginAmt;
    }

    public StatusYesNo getRevenueOverride() {
        return revenueOverride;
    }

    public void setRevenueOverride(StatusYesNo revenueOverride) {
        this.revenueOverride = revenueOverride;
    }

    public StatusYesNo getCostOverride() {
        return costOverride;
    }

    public void setCostOverride(StatusYesNo costOverride) {
        this.costOverride = costOverride;
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

    public AuditShipmentCostDetailsEntity getAuditShipmentCostDetails() {
        return auditShipmentCostDetails;
    }

    public void setAuditShipmentCostDetails(AuditShipmentCostDetailsEntity auditShipmentCostDetails) {
        this.auditShipmentCostDetails = auditShipmentCostDetails;
    }

    /**
     * Method makes a deep copy of {@link LoadCostDetailsEntity}.
     * 
     * @return a copy of {@link LoadCostDetailsEntity}.
     */
    public LoadCostDetailsEntity copy() {
        LoadCostDetailsEntity newActiveCostDetail = new LoadCostDetailsEntity();
        newActiveCostDetail.setGeneralLedgerDate(getGeneralLedgerDate());
        newActiveCostDetail.setGuaranteedBy(getGuaranteedBy());
        newActiveCostDetail.setGuaranteedNameForBOL(getGuaranteedNameForBOL());
        newActiveCostDetail.setInvoiceNumber(getInvoiceNumber());
        newActiveCostDetail.setGroupInvoiceNumber(getGroupInvoiceNumber());
        newActiveCostDetail.setNewLiability(getNewLiability());
        newActiveCostDetail.setPricingProfileDetailId(getPricingProfileDetailId());
        newActiveCostDetail.setRevenueOverride(getRevenueOverride());
        newActiveCostDetail.setCostOverride(getCostOverride());
        newActiveCostDetail.setProhibitedCommodities(getProhibitedCommodities());
        newActiveCostDetail.setSentToFinance(getSentToFinance());
        newActiveCostDetail.setServiceType(getServiceType());
        newActiveCostDetail.setShipDate(getShipDate());
        newActiveCostDetail.setStopOffs(getStopOffs());
        newActiveCostDetail.setUsedLiability(getUsedLiability());
        newActiveCostDetail.setStatus(Status.ACTIVE);
        newActiveCostDetail.setTotalCost(getTotalCost());
        newActiveCostDetail.setTotalRevenue(getTotalRevenue());
        newActiveCostDetail.setLoad(getLoad());
        newActiveCostDetail.setAuditShipmentCostDetails(getAuditShipmentCostDetails());
        for (CostDetailItemEntity item : getCostDetailItems()) {
            CostDetailItemEntity newCostItem = item.copy();
            newCostItem.setCostDetails(newActiveCostDetail);
            newActiveCostDetail.getCostDetailItems().add(newCostItem);
        }
        return newActiveCostDetail;
    }
}

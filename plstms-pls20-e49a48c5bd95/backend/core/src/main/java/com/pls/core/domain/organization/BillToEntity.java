package com.pls.core.domain.organization;


import java.math.BigDecimal;
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

import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.Currency;

/**
 * Represents object where Bill is sent.
 *
 * @author Denis Zhupinsky (TEAM International)
 */
@Entity
@Table(name = "BILL_TO")
public class BillToEntity implements Identifiable<Long>, HasModificationInfo {

    public static final String Q_GET_DETAILS_AX = "com.pls.core.domain.organization.BillToEntity.Q_GET_DETAILS_AX";
    public static final String Q_GET_ID_AND_NAME_BY_ORG_ID = "com.pls.core.domain.organization.BillToEntity.Q_GET_ID_AND_NAME_BY_ORG_ID";
    public static final String Q_GET_EMAILS = "com.pls.core.domain.organization.BillToEntity.Q_GET_EMAILS";
    public static final String Q_GET_BILLTO_BY_NAME_AND_ORG_ID = "com.pls.core.domain.organization.BillToEntity.Q_GET_BILLTO_BY_NAME_AND_ORG_ID";
    public static final String Q_GET_FOR_SHIPMENT = "com.pls.core.domain.organization.BillToEntity.Q_GET_FOR_SHIPMENT";
    public static final String Q_GET_FOR_CUSTOMER = "com.pls.core.domain.organization.BillToEntity.Q_GET_FOR_CUSTOMER";
    public static final String Q_GET_DEFAULT_VALUES_BY_ID = "com.pls.core.domain.organization.BillToEntity.Q_GET_DEFAULT_VALUES_BY_ID";
    public static final String Q_GET_REPROCESS_EMAIL_OPTION = "com.pls.core.domain.organization.BillToEntity.Q_GET_REPROCESS_EMAIL_OPTION";

    private static final long serialVersionUID = 4715026975336890245L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bto_sequence")
    @SequenceGenerator(name = "bto_sequence", sequenceName = "BTO_SEQ", allocationSize = 1)
    @Column(name = "BILL_TO_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORG_ID")
    private OrganizationEntity organization;

    @OneToMany(mappedBy = "billTo", fetch = FetchType.LAZY)
    private Set<OrganizationLocationEntity> locations;

    /**
     * [Hima]: Its one-to-one relationship between BILL_TO and BILLING_INVOICE_NODE tables.
     */
    @OneToOne(mappedBy = "billTo", cascade = CascadeType.ALL, orphanRemoval = true)
    private BillingInvoiceNodeEntity billingInvoiceNode;

    @Column(name = "NAME")
    private String name;

    @Column(name = "IS_DEFAULT")
    @Type(type = "yes_no")
    private Boolean defaultNode;

    @OneToOne(mappedBy = "billTo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private InvoiceSettingsEntity invoiceSettings;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAY_TERMS_ID")
    private PlsCustomerTermsEntity plsCustomerTerms;

    @Column(name = "REQUIRED_AUDIT", columnDefinition = "TINYINT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean auditPrefReq;

    @Column(name = "EMAIL_ACCOUNT_EXECUTIVE", columnDefinition = "TINYINT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean emailAccountExecutive;

    @Column(name = "AUDIT_INSTRUCTIONS")
    private String auditInstructions;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "billTo", fetch = FetchType.LAZY)
    private Set<BillToRequiredFieldEntity> billToRequiredField = new HashSet<BillToRequiredFieldEntity>();

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "billTo", cascade = CascadeType.ALL)
    private BillToThresholdSettingsEntity billToThresholdSettings;

    @OneToOne(mappedBy = "billTo", cascade = CascadeType.ALL)
    private BillToDefaultValuesEntity billToDefaultValues;

    @Column(name = "CURRENCY_CODE")
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Version
    @Column(name = "VERSION")
    private long version = 1;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "billTo")
    private EdiSettingsEntity ediSettings;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "billTo")
    private UnbilledRevenueEntity unbilledRevenue;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "billTo")
    private CreditLimitEntity creditLimit;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "billTo")
    private OpenBalanceEntity openBalance;

    @Column(name = "CREDIT_HOLD")
    @Type(type = "yes_no")
    private Boolean creditHold = Boolean.FALSE;

    @Column(name = "OVERRIDE_CREDIT_HOLD")
    @Type(type = "yes_no")
    private Boolean overrideCreditHold = Boolean.FALSE;

    @Column(name = "AUTO_CREDIT_HOLD")
    @Type(type = "yes_no")
    private Boolean autoCreditHold = Boolean.FALSE;

    @Column(name = "PAYMENT_METHOD")
    private String paymentMethod;

    @Column(name = "SEND_INVOICES_REPORTS")
    @Type(type = "yes_no")
    private Boolean sendInvoicesReports = Boolean.FALSE;

    /*
     * Email for Credit Card Communication
     */
    @Column(name = "CREDIT_CARD_EMAIL")
    private String creditCardEmail;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get organization for this BillTo.
     *
     * @return the organization for this BillTo.
     */
    public OrganizationEntity getOrganization() {
        return organization;
    }

    /**
     * Set organization for this BillTo.
     *
     * @param organization the organization for this BillTo.
     */
    public void setOrganization(OrganizationEntity organization) {
        this.organization = organization;
    }

    public Set<OrganizationLocationEntity> getLocations() {
        return locations;
    }

    public void setLocations(Set<OrganizationLocationEntity> locations) {
        this.locations = locations;
    }

    /**
     * Getter for the field billingInvoiceNode.
     *
     * @return the value for the field billingInvoiceNode.
     */
    public BillingInvoiceNodeEntity getBillingInvoiceNode() {
        return billingInvoiceNode;
    }

    /**
     * Setter for the field billingInvoiceNode.
     *
     * @param billingInvoiceNode the value to set for the field.
     */
    public void setBillingInvoiceNode(BillingInvoiceNodeEntity billingInvoiceNode) {
        this.billingInvoiceNode = billingInvoiceNode;
    }

    /**
     * Getter for the field name.
     *
     * @return the value for the field name.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the field name.
     *
     * @param name the value to set for the field.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return if bill to is default.
     *
     * @return true if bill to is default.
     */
    public boolean isDefaultNode() {
        return defaultNode != null && defaultNode;
    }

    /**
     * Setter for the field defaultNode.
     *
     * @param defaultNode the value to set for the field.
     */
    public void setDefaultNode(boolean defaultNode) {
        this.defaultNode = defaultNode;
    }

    public InvoiceSettingsEntity getInvoiceSettings() {
        return invoiceSettings;
    }

    public void setInvoiceSettings(InvoiceSettingsEntity invoiceSettings) {
        this.invoiceSettings = invoiceSettings;
    }

    public PlsCustomerTermsEntity getPlsCustomerTerms() {
        return plsCustomerTerms;
    }

    public void setPlsCustomerTerms(PlsCustomerTermsEntity plsCustomerTerms) {
        this.plsCustomerTerms = plsCustomerTerms;
    }

    public Boolean isAuditPrefReq() {
        return auditPrefReq;
    }

    public void setAuditPrefReq(Boolean auditPrefReq) {
        this.auditPrefReq = auditPrefReq;
    }

    public Boolean getEmailAccountExecutive() {
        return emailAccountExecutive;
    }

    public void setEmailAccountExecutive(Boolean emailAccountExecutive) {
        this.emailAccountExecutive = emailAccountExecutive;
    }

    public String getAuditInstructions() {
        return auditInstructions;
    }

    public void setAuditInstructions(String auditInstructions) {
        this.auditInstructions = auditInstructions;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public Set<BillToRequiredFieldEntity> getBillToRequiredField() {
        return billToRequiredField;
    }

    public void setBillToRequiredField(Set<BillToRequiredFieldEntity> billToRequiredField) {
        this.billToRequiredField = billToRequiredField;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public EdiSettingsEntity getEdiSettings() {
        return ediSettings;
    }

    public void setEdiSettings(EdiSettingsEntity ediSettings) {
        this.ediSettings = ediSettings;
    }

    public UnbilledRevenueEntity getUnbilledRevenue() {
        return unbilledRevenue;
    }

    public void setUnbilledRevenue(UnbilledRevenueEntity unbilledRevenue) {
        this.unbilledRevenue = unbilledRevenue;
    }

    public CreditLimitEntity getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(CreditLimitEntity creditLimit) {
        this.creditLimit = creditLimit;
    }

    public OpenBalanceEntity getOpenBalance() {
        return openBalance;
    }

    public void setOpenBalance(OpenBalanceEntity openBalance) {
        this.openBalance = openBalance;
    }

    public Boolean getCreditHold() {
        return creditHold;
    }

    public Boolean getOverrideCreditHold() {
        return overrideCreditHold;
    }

    public Boolean getAutoCreditHold() {
        return autoCreditHold;
    }

    public void setCreditHold(Boolean creditHold) {
        this.creditHold = creditHold;
    }

    public void setOverrideCreditHold(Boolean overrideCreditHold) {
        this.overrideCreditHold = overrideCreditHold;
    }

    public void setAutoCreditHold(Boolean autoCreditHold) {
        this.autoCreditHold = autoCreditHold;
    }

    public BillToThresholdSettingsEntity getBillToThresholdSettings() {
        return billToThresholdSettings;
    }

    public void setBillToThresholdSettings(BillToThresholdSettingsEntity billToThresholdSettings) {
        this.billToThresholdSettings = billToThresholdSettings;
    }

    public BillToDefaultValuesEntity getBillToDefaultValues() {
        return billToDefaultValues;
    }

    public void setBillToDefaultValues(BillToDefaultValuesEntity billToDefaultValues) {
        this.billToDefaultValues = billToDefaultValues;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCreditCardEmail() {
        return creditCardEmail;
    }

    public void setCreditCardEmail(String creditCardEmail) {
        this.creditCardEmail = creditCardEmail;
    }

    public Boolean isSendInvoicesReports() {
        return sendInvoicesReports;
    }

    public void setSendInvoicesReports(Boolean sendInvoicesReports) {
        this.sendInvoicesReports = sendInvoicesReports;
    }

    /**
     * Returns available Credit Amount.
     * 
     * @return available credit amount.
     */
    public BigDecimal getAvailableCreditAmount() {
        BigDecimal lCreditLimit = BigDecimal.ZERO;
        if (creditLimit != null) {
            lCreditLimit = ObjectUtils.defaultIfNull(creditLimit.getCreditLimit(), BigDecimal.ZERO);
        }

        BigDecimal lUnbilledRevenue = BigDecimal.ZERO;
        if (unbilledRevenue != null) {
            lUnbilledRevenue = ObjectUtils.defaultIfNull(unbilledRevenue.getUnbilledRevenue(), BigDecimal.ZERO);
        }

        BigDecimal lOpenBalance = BigDecimal.ZERO;
        if (openBalance != null) {
            lOpenBalance = ObjectUtils.defaultIfNull(openBalance.getBalance(), BigDecimal.ZERO);
        }

        lUnbilledRevenue = lOpenBalance.add(lUnbilledRevenue);

        return lCreditLimit.subtract(lUnbilledRevenue);
    }
}
